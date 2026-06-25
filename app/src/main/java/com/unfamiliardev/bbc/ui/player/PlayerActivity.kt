/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.databinding.ActivityPlayerBinding
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.util.KonamiCodeDetector
import com.unfamiliardev.bbc.util.LocaleHelper
import com.unfamiliardev.bbc.util.RecentlyWatchedStore

class PlayerActivity : FragmentActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    private var player: ExoPlayer? = null
    private var isInPip = false

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    private val sleepHandler = Handler(Looper.getMainLooper())
    private var sleepRunnable: Runnable? = null
    private var sleepEndMs = 0L
    private val sleepTickRunnable = object : Runnable {
        override fun run() {
            if (sleepEndMs == 0L) return
            val remaining = ((sleepEndMs - System.currentTimeMillis()) / 1000 / 60).coerceAtLeast(0)
            binding.sleepCountdown.text = "😴  ${remaining}m"
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url  = intent.getStringExtra(EXTRA_URL)  ?: run { finish(); return }
        val name = intent.getStringExtra(EXTRA_NAME) ?: ""

        viewModel.setChannel(name, url)
        binding.channelTitle.text = name

        saveLastPlayed(url, name)
        RecentlyWatchedStore.record(
            this,
            Channel(
                id         = url,
                name       = name,
                url        = url,
                logoUrl    = intent.getStringExtra(EXTRA_LOGO),
                group      = intent.getStringExtra(EXTRA_GROUP) ?: "",
                playlistId = intent.getLongExtra(EXTRA_PLAYLIST_ID, -1L)
            )
        )
        initPlayer(url)
    }

    private fun initPlayer(url: String) {
        player = ExoPlayer.Builder(this).build().also { exo ->
            binding.playerView.player = exo
            exo.setMediaItem(MediaItem.fromUri(url))
            exo.prepare()
            exo.playWhenReady = true

            exo.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    binding.bufferingIndicator.visibility =
                        if (state == Player.STATE_BUFFERING) View.VISIBLE else View.GONE
                    if (state == Player.STATE_BUFFERING) binding.errorText.visibility = View.GONE
                }

                override fun onPlayerError(error: PlaybackException) {
                    binding.bufferingIndicator.visibility = View.GONE
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = "Playback error: ${error.errorCodeName}"
                }
            })
        }
    }

    // ── Sleep timer ──────────────────────────────────────────────────────────

    fun setSleepTimer(minutes: Int) {
        sleepRunnable?.let { sleepHandler.removeCallbacks(it) }
        sleepHandler.removeCallbacks(sleepTickRunnable)

        if (minutes <= 0) {
            sleepEndMs = 0L
            binding.sleepCountdown.visibility = View.GONE
            return
        }

        sleepEndMs = System.currentTimeMillis() + minutes * 60_000L
        val runnable = Runnable {
            player?.pause()
            finish()
        }
        sleepRunnable = runnable
        sleepHandler.postDelayed(runnable, minutes * 60_000L)

        binding.sleepCountdown.visibility = View.VISIBLE
        sleepTickRunnable.run()
    }

    // ── Stream info ───────────────────────────────────────────────────────────

    fun toggleStreamInfo() {
        if (binding.streamInfoOverlay.visibility == View.VISIBLE) {
            binding.streamInfoOverlay.visibility = View.GONE
            return
        }
        val exo = player ?: return
        val video = exo.videoFormat
        val audio = exo.audioFormat

        binding.infoResolution.text = if (video != null)
            "Video: ${video.width}×${video.height} @ ${"%.1f".format(video.frameRate)}fps"
        else "Video: unknown"

        binding.infoVideoCodec.text = "Codec: ${video?.sampleMimeType?.substringAfter("video/") ?: "—"}"

        binding.infoBitrate.text = if ((video?.bitrate ?: 0) > 0)
            "Bitrate: ${video!!.bitrate / 1000} kbps"
        else "Bitrate: —"

        binding.infoAudio.text = if (audio != null)
            "Audio: ${audio.sampleMimeType?.substringAfter("audio/") ?: "?"} ${audio.channelCount}ch ${audio.sampleRate}Hz"
        else "Audio: —"

        binding.infoUrl.text = intent.getStringExtra(EXTRA_URL) ?: ""

        binding.streamInfoOverlay.visibility = View.VISIBLE
    }

    // ── Aspect ratio ──────────────────────────────────────────────────────────

    fun cycleAspectRatio() {
        currentAspectIndex = (currentAspectIndex + 1) % aspectModes.size
        binding.playerView.resizeMode = aspectModes[currentAspectIndex]

        binding.aspectBadge.text = aspectLabels[currentAspectIndex]
        binding.aspectBadge.visibility = View.VISIBLE

        aspectBadgeHideHandler.removeCallbacksAndMessages(null)
        aspectBadgeHideHandler.postDelayed({ binding.aspectBadge.visibility = View.GONE }, 2000)
    }

    // ── Track selection ───────────────────────────────────────────────────────

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
                        .buildUpon()
                        .setTrackTypeDisabled(targetType, true)
                        .build()
                } else {
                    val group = exoPlayer.currentTracks.groups.getOrNull(groupIdx)
                    if (group != null) {
                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                            .buildUpon()
                            .setTrackTypeDisabled(targetType, false)
                            .addOverride(
                                TrackSelectionParameters.TrackSelectionOverride(
                                    group.mediaTrackGroup, listOf(trackIdx)
                                )
                            )
                            .build()
                    }
                }
            }
        }

        GuidedStepSupportFragment.add(
            supportFragmentManager,
            TrackSelectorFragment.newInstance(type)
        )
    }

    // ── PiP ──────────────────────────────────────────────────────────────────

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPip()
    }

    private fun enterPip() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && player?.isPlaying == true) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPip = isInPictureInPictureMode
        val v = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        binding.channelTitle.visibility = v
        binding.sleepCountdown.visibility = if (isInPictureInPictureMode || sleepEndMs == 0L) View.GONE else View.VISIBLE
        if (isInPictureInPictureMode) {
            binding.bufferingIndicator.visibility = View.GONE
            binding.errorText.visibility = View.GONE
            binding.streamInfoOverlay.visibility = View.GONE
        }
    }

    // ── Key handling ─────────────────────────────────────────────────────────

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isInPip) return super.onKeyDown(keyCode, event)
        if (konamiDetector.onKeyDown(keyCode)) return true

        return when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                player?.let { if (it.isPlaying) it.pause() else it.play() }
                true
            }
            KeyEvent.KEYCODE_MEDIA_PLAY  -> { player?.play(); true }
            KeyEvent.KEYCODE_MEDIA_PAUSE -> { player?.pause(); true }

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

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    private fun saveLastPlayed(url: String, name: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_URL, url)
            .putString(KEY_LAST_NAME, name)
            .apply()
    }

    override fun onPause() {
        super.onPause()
        if (!isInPip) player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        sleepHandler.removeCallbacksAndMessages(null)
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

        private const val SEEK_MS   = 15_000L

        fun getLastPlayed(context: Context): Pair<String, String>? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val url  = prefs.getString(KEY_LAST_URL, null) ?: return null
            val name = prefs.getString(KEY_LAST_NAME, "") ?: ""
            return url to name
        }
    }
}
