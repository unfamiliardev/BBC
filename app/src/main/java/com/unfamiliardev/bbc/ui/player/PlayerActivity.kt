package com.unfamiliardev.bbc.ui.player

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Rational
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.repository.EpgRepository
import com.unfamiliardev.bbc.databinding.ActivityPlayerBinding
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.util.KonamiCodeDetector
import com.unfamiliardev.bbc.util.LocaleHelper
import com.unfamiliardev.bbc.util.RecentlyWatchedStore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlayerActivity : FragmentActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var isInPip = false
    internal var currentUrl: String = ""
    private var currentLogoUrl: String? = null
    var isMuted = false
    var currentPlaybackSpeed = 1f

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    private val osdHandler = Handler(Looper.getMainLooper())
    private val osdHideRunnable = Runnable { hideOsd() }

    private fun showOsd() {
        if (isInPip) return
        binding.bottomOsd.visibility = View.VISIBLE
        osdHandler.removeCallbacks(osdHideRunnable)
        osdHandler.postDelayed(osdHideRunnable, OSD_HIDE_DELAY_MS)
    }

    private fun hideOsd() {
        binding.bottomOsd.visibility = View.GONE
    }

    private val clockHandler = Handler(Looper.getMainLooper())
    private val clockFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val clockRunnable = object : Runnable {
        override fun run() {
            binding.playerClock.text = clockFormat.format(Date())
            clockHandler.postDelayed(this, 30_000)
        }
    }

    private val sleepHandler = Handler(Looper.getMainLooper())
    private var sleepRunnable: Runnable? = null
    private var sleepEndMs = 0L
    private val sleepTickRunnable = object : Runnable {
        override fun run() {
            if (sleepEndMs == 0L) return
            val remaining = ((sleepEndMs - System.currentTimeMillis()) / 1000 / 60).coerceAtLeast(0)
            binding.sleepCountdown.text = "Sleep: ${remaining}m"
            if (remaining > 0) sleepHandler.postDelayed(this, 30_000)
        }
    }

    private val aspectModes = intArrayOf(
        AspectRatioFrameLayout.RESIZE_MODE_FIT,
        AspectRatioFrameLayout.RESIZE_MODE_FILL,
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
    )
    private val aspectLabels = arrayOf("Fit", "Fill", "Zoom")
    private var currentAspectIndex = 0
    private val aspectBadgeHideHandler = Handler(Looper.getMainLooper())

    private val zapHandler = Handler(Looper.getMainLooper())

    private val reconnectHandler = Handler(Looper.getMainLooper())
    private var bufferingStartMs = 0L

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url   = intent.getStringExtra(EXTRA_URL)  ?: run { finish(); return }
        val name  = intent.getStringExtra(EXTRA_NAME) ?: ""
        val logo  = intent.getStringExtra(EXTRA_LOGO)
        val group = intent.getStringExtra(EXTRA_GROUP) ?: ""
        currentUrl = url
        currentLogoUrl = logo

        viewModel.setChannel(name, url)
        applyChannelInfo(name, group, logo)
        clockRunnable.run()

        saveLastPlayed(url, name)
        RecentlyWatchedStore.record(this, Channel(
            id = url, name = name, url = url,
            logoUrl = logo, group = group,
            playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L)
        ))
        loadNowPlaying(name)
        initPlayer(url)
        showOsd()
    }

    private fun applyChannelInfo(name: String, group: String, logoUrl: String?) {
        binding.channelTitle.text = name
        if (group.isNotEmpty()) {
            binding.channelGroup.text = group
            binding.channelGroup.visibility = View.VISIBLE
        } else {
            binding.channelGroup.visibility = View.GONE
        }
        if (!logoUrl.isNullOrEmpty()) {
            Glide.with(this).load(logoUrl)
                .into(binding.osdLogo)
            binding.osdLogo.visibility = View.VISIBLE
        } else {
            binding.osdLogo.visibility = View.GONE
        }
    }

    private fun loadNowPlaying(channelName: String) {
        val now = EpgRepository.getNowPlaying(channelName)?.title
        if (now != null) {
            binding.channelNowPlaying.text = now
            binding.nowPlayingRow.visibility = View.VISIBLE
        } else {
            binding.nowPlayingRow.visibility = View.GONE
        }
    }

    private fun loadChannel(channel: Channel) {
        currentUrl = channel.url
        currentLogoUrl = channel.logoUrl
        applyChannelInfo(channel.name, channel.group, channel.logoUrl)
        loadNowPlaying(channel.name)
        saveLastPlayed(channel.url, channel.name)
        RecentlyWatchedStore.record(this, channel)
        initPlayer(channel.url)
        showOsd()
    }

    private fun initPlayer(url: String) {
        reconnectHandler.removeCallbacksAndMessages(null)
        player?.release()
        binding.errorContainer.visibility = View.GONE
        binding.bufferingIndicator.visibility = View.GONE
        bufferingStartMs = 0L

        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            exo.playWhenReady = true
            exo.volume = if (isMuted) 0f else 1f
            exo.setPlaybackSpeed(currentPlaybackSpeed)

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_BUFFERING -> {
                            binding.bufferingIndicator.visibility = View.VISIBLE
                            binding.errorContainer.visibility = View.GONE
                            if (bufferingStartMs == 0L) {
                                bufferingStartMs = System.currentTimeMillis()
                                reconnectHandler.postDelayed({
                                    if (player?.playbackState == Player.STATE_BUFFERING) {
                                        initPlayer(currentUrl)
                                    }
                                }, AUTO_RECONNECT_MS)
                            }
                        }
                        Player.STATE_READY -> {
                            binding.bufferingIndicator.visibility = View.GONE
                            reconnectHandler.removeCallbacksAndMessages(null)
                            bufferingStartMs = 0L
                            showOsd()
                        }
                        else -> binding.bufferingIndicator.visibility = View.GONE
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    reconnectHandler.removeCallbacksAndMessages(null)
                    binding.bufferingIndicator.visibility = View.GONE
                    binding.errorText.text = error.errorCodeName
                    binding.errorContainer.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun showZap(direction: String, channelName: String) {
        binding.zapDirection.text = direction
        binding.zapChannelName.text = channelName
        binding.zapOverlay.visibility = View.VISIBLE
        zapHandler.removeCallbacksAndMessages(null)
        zapHandler.postDelayed({ binding.zapOverlay.visibility = View.GONE }, 1500)
    }

    fun setSleepTimer(minutes: Int) {
        sleepRunnable?.let { sleepHandler.removeCallbacks(it) }
        sleepHandler.removeCallbacks(sleepTickRunnable)
        if (minutes <= 0) {
            sleepEndMs = 0L
            binding.sleepCountdown.visibility = View.GONE
            return
        }
        sleepEndMs = System.currentTimeMillis() + minutes * 60_000L
        val runnable = Runnable { player?.pause(); finish() }
        sleepRunnable = runnable
        sleepHandler.postDelayed(runnable, minutes * 60_000L)
        binding.sleepCountdown.visibility = View.VISIBLE
        sleepTickRunnable.run()
    }

    fun toggleStreamInfo() {
        if (binding.streamInfoOverlay.visibility == View.VISIBLE) {
            binding.streamInfoOverlay.visibility = View.GONE
            return
        }
        val exo = player ?: return
        val video = exo.videoFormat
        val audio = exo.audioFormat

        binding.infoResolution.text = if (video != null)
            "${video.width}x${video.height}  ${"%,.0f".format(video.frameRate)}fps"
        else "Video: unknown"
        binding.infoVideoCodec.text =
            video?.sampleMimeType?.substringAfter("video/")?.uppercase() ?: "—"
        binding.infoBitrate.text = if ((video?.bitrate ?: 0) > 0)
            "${video!!.bitrate / 1000} kbps"
        else "Bitrate: —"
        binding.infoAudio.text = if (audio != null)
            "${audio.sampleMimeType?.substringAfter("audio/")?.uppercase() ?: "?"} · ${audio.channelCount}ch · ${audio.sampleRate / 1000}kHz"
        else "Audio: —"
        binding.infoUrl.text = currentUrl
        binding.streamInfoOverlay.visibility = View.VISIBLE
    }

    fun cycleAspectRatio() {
        currentAspectIndex = (currentAspectIndex + 1) % aspectModes.size
        binding.playerView.resizeMode = aspectModes[currentAspectIndex]
        binding.aspectBadge.text = aspectLabels[currentAspectIndex]
        binding.aspectBadge.visibility = View.VISIBLE
        aspectBadgeHideHandler.removeCallbacksAndMessages(null)
        aspectBadgeHideHandler.postDelayed({ binding.aspectBadge.visibility = View.GONE }, 2000)
    }

    fun isCurrentStreamSeekable() = player?.isCurrentMediaItemSeekable == true

    fun toggleMute() {
        val exo = player ?: return
        isMuted = !isMuted
        exo.volume = if (isMuted) 0f else 1f
        binding.muteBadge.visibility = if (isMuted) View.VISIBLE else View.GONE
    }

    fun reloadStream() = initPlayer(currentUrl)

    fun setPlaybackSpeed(speed: Float) {
        currentPlaybackSpeed = speed
        player?.setPlaybackSpeed(speed)
    }

    fun openTrackSelector(type: String) {
        val exo = player ?: return
        val targetType = if (type == "audio") C.TRACK_TYPE_AUDIO else C.TRACK_TYPE_TEXT
        val tracks = exo.currentTracks.groups
            .filter { it.type == targetType }
            .flatMapIndexed { groupIdx, group ->
                (0 until group.length).map { trackIdx ->
                    val format = group.getTrackFormat(trackIdx)
                    val label = buildString {
                        append(format.language?.uppercase() ?: "Track $trackIdx")
                        if (format.label != null) append(" — ${format.label}")
                        if (type == "audio" && format.channelCount > 0)
                            append(" (${format.channelCount}ch)")
                    }
                    TrackInfo(groupIdx, trackIdx, label)
                }
            }
        TrackSelectorFragment.tracks = tracks
        TrackSelectorFragment.onTrackSelected = { groupIdx, trackIdx ->
            val exoPlayer = player
            if (exoPlayer != null) {
                if (groupIdx == -1) {
                    exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                        .buildUpon().setTrackTypeDisabled(targetType, true).build()
                } else {
                    val group = exoPlayer.currentTracks.groups.getOrNull(groupIdx)
                    if (group != null) {
                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                            .buildUpon()
                            .setTrackTypeDisabled(targetType, false)
                            .addOverride(TrackSelectionOverride(group.mediaTrackGroup, listOf(trackIdx)))
                            .build()
                    }
                }
            }
        }
        GuidedStepSupportFragment.add(supportFragmentManager, TrackSelectorFragment.newInstance(type))
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && player?.isPlaying == true) {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build()
            )
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPip = isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            osdHandler.removeCallbacks(osdHideRunnable)
            hideOsd()
            binding.bufferingIndicator.visibility = View.GONE
            binding.errorContainer.visibility = View.GONE
            binding.streamInfoOverlay.visibility = View.GONE
        } else {
            showOsd()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isInPip) return super.onKeyDown(keyCode, event)
        if (konamiDetector.onKeyDown(keyCode)) return true
        showOsd()

        return when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (binding.errorContainer.visibility == View.VISIBLE) {
                    initPlayer(currentUrl)
                } else {
                    player?.let { if (it.isPlaying) it.pause() else it.play() }
                }
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY  -> { player?.play(); true }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> { player?.pause(); true }
            KeyEvent.KEYCODE_DPAD_UP -> {
                val ch = PlayerQueue.prev()
                if (ch != null) { showZap("Channel up", ch.name); loadChannel(ch) }
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val ch = PlayerQueue.next()
                if (ch != null) { showZap("Channel down", ch.name); loadChannel(ch) }
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                player?.let { if (it.isCurrentMediaItemSeekable) it.seekTo(it.currentPosition + SEEK_MS) }
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                player?.let { if (it.isCurrentMediaItemSeekable) it.seekTo((it.currentPosition - SEEK_MS).coerceAtLeast(0)) }
                true
            }
            KeyEvent.KEYCODE_INFO -> { toggleStreamInfo(); true }
            KeyEvent.KEYCODE_MENU -> {
                GuidedStepSupportFragment.add(supportFragmentManager, PlayerMenuFragment())
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    private fun saveLastPlayed(url: String, name: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_URL, url)
            .putString(KEY_LAST_NAME, name)
            .apply()
    }

    override fun onPause() {
        super.onPause()
        if (!isInPip) player?.pause()
        osdHandler.removeCallbacks(osdHideRunnable)
        clockHandler.removeCallbacks(clockRunnable)
    }

    override fun onResume() {
        super.onResume()
        clockRunnable.run()
        showOsd()
    }

    override fun onDestroy() {
        super.onDestroy()
        osdHandler.removeCallbacksAndMessages(null)
        sleepHandler.removeCallbacksAndMessages(null)
        aspectBadgeHideHandler.removeCallbacksAndMessages(null)
        clockHandler.removeCallbacksAndMessages(null)
        zapHandler.removeCallbacksAndMessages(null)
        reconnectHandler.removeCallbacksAndMessages(null)
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_URL         = "extra_url"
        const val EXTRA_NAME        = "extra_name"
        const val EXTRA_LOGO        = "extra_logo"
        const val EXTRA_GROUP       = "extra_group"
        const val EXTRA_PLAYLIST_ID = "extra_playlist_id"
        const val PREFS_NAME        = "bbc_player"
        const val KEY_LAST_URL      = "last_url"
        const val KEY_LAST_NAME     = "last_name"

        private const val SEEK_MS             = 15_000L
        private const val OSD_HIDE_DELAY_MS   = 7_000L
        private const val AUTO_RECONNECT_MS   = 30_000L

        fun getLastPlayed(context: Context): Pair<String, String>? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val url  = prefs.getString(KEY_LAST_URL, null) ?: return null
            val name = prefs.getString(KEY_LAST_NAME, "") ?: ""
            return url to name
        }
    }
}
