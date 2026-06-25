/*
 * BBC â€” Open-source Android TV IPTV client
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
import android.util.Rational
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(EXTRA_URL) ?: run { finish(); return }
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

    // â”€â”€ PiP â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

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
        val overlayVisibility = if (isInPictureInPictureMode) View.GONE else View.VISIBLE
        binding.channelTitle.visibility = overlayVisibility
        if (!isInPictureInPictureMode) {
            // Restore error/buffer state visibility when exiting PiP
            binding.errorText.visibility =
                if (binding.errorText.text.isNotEmpty()) View.VISIBLE else View.GONE
        } else {
            binding.bufferingIndicator.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }
    }

    // â”€â”€ Lifecycle â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private fun saveLastPlayed(url: String, name: String) {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_LAST_URL, url)
            .putString(KEY_LAST_NAME, name)
            .apply()
    }

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
            else -> super.onKeyDown(keyCode, event)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isInPip) player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
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

        fun getLastPlayed(context: Context): Pair<String, String>? {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val url = prefs.getString(KEY_LAST_URL, null) ?: return null
            val name = prefs.getString(KEY_LAST_NAME, "") ?: ""
            return url to name
        }
    }
}
