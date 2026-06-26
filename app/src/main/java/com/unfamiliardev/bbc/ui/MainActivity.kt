package com.unfamiliardev.bbc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.repository.EpgRepository
import com.unfamiliardev.bbc.ui.browse.ChannelOptionsFragment
import com.unfamiliardev.bbc.ui.channels.ChannelsFragment
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.ui.epg.EpgFragment
import com.unfamiliardev.bbc.ui.player.PlayerActivity
import com.unfamiliardev.bbc.ui.settings.SettingsActivity
import com.unfamiliardev.bbc.ui.update.UpdateFragment
import com.unfamiliardev.bbc.util.AppSettings
import com.unfamiliardev.bbc.util.FavouritesStore
import com.unfamiliardev.bbc.util.KonamiCodeDetector
import com.unfamiliardev.bbc.util.LocaleHelper
import com.unfamiliardev.bbc.util.UpdateChecker
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    private var longPressConsumed = false
    private var currentPreviewChannel: Channel? = null

    private lateinit var previewLogo: ImageView
    private lateinit var previewName: TextView
    private lateinit var previewGroup: TextView
    private lateinit var previewNowLabel: TextView
    private lateinit var previewNow: TextView
    private lateinit var previewFavBtn: TextView
    private lateinit var previewHint: TextView

    private lateinit var navRecent: TextView
    private lateinit var navChannels: TextView
    private lateinit var navEpg: TextView
    private lateinit var navVod: TextView
    private lateinit var navSettings: TextView

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        setupSidebar()
        resetPreviewPanel()

        if (savedInstanceState == null) {
            when (AppSettings.getStartupTab(this)) {
                AppSettings.TAB_VOD -> { setNavSelected(navVod); showVod() }
                AppSettings.TAB_RECENT -> { setNavSelected(navRecent); showChannelsFiltered(recentOnly = true) }
                AppSettings.TAB_EPG -> { setNavSelected(navEpg); showEpg() }
                else -> showChannels()
            }

            if (AppSettings.getAutoplay(this)) {
                PlayerActivity.getLastPlayed(this)?.let { (url, name) ->
                    startActivity(Intent(this, PlayerActivity::class.java).apply {
                        putExtra(PlayerActivity.EXTRA_URL, url)
                        putExtra(PlayerActivity.EXTRA_NAME, name)
                    })
                }
            }

            checkForUpdate()
        }
    }

    private fun bindViews() {
        previewLogo = findViewById(R.id.preview_logo)
        previewName = findViewById(R.id.preview_channel_name)
        previewGroup = findViewById(R.id.preview_group)
        previewNowLabel = findViewById(R.id.preview_now_label)
        previewNow = findViewById(R.id.preview_now_playing)
        previewFavBtn = findViewById(R.id.preview_fav_btn)
        previewHint = findViewById(R.id.preview_hint)

        navRecent = findViewById<TextView>(R.id.nav_recent)
        navChannels = findViewById<TextView>(R.id.nav_channels)
        navEpg = findViewById<TextView>(R.id.nav_epg)
        navVod = findViewById<TextView>(R.id.nav_vod)
        navSettings = findViewById<TextView>(R.id.nav_settings)
    }

    private fun setupSidebar() {
        navRecent.setOnClickListener {
            setNavSelected(navRecent)
            showChannelsFiltered(recentOnly = true)
        }
        navChannels.setOnClickListener {
            setNavSelected(navChannels)
            showChannels()
        }
        navEpg.setOnClickListener {
            setNavSelected(navEpg)
            showEpg()
        }
        navVod.setOnClickListener {
            setNavSelected(navVod)
            showVod()
        }
        navSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        navChannels.isSelected = true
    }

    private fun setNavSelected(selected: ImageButton) {
        navRecent.isSelected = false
        navChannels.isSelected = false
        navEpg.isSelected = false
        navVod.isSelected = false
        selected.isSelected = true
    }

    private fun showChannels() {
        swapContent(ChannelsFragment.newInstance(recentOnly = false))
    }

    private fun showChannelsFiltered(recentOnly: Boolean) {
        swapContent(ChannelsFragment.newInstance(recentOnly = recentOnly))
    }

    private fun showEpg() {
        swapContent(EpgFragment())
    }

    private fun showVod() {
        swapContent(ChannelsFragment.newInstance(vodOnly = true))
    }

    private fun swapContent(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }

    private fun checkForUpdate() {
        lifecycleScope.launch {
            val info = UpdateChecker.check(this@MainActivity) ?: return@launch
            GuidedStepSupportFragment.add(
                supportFragmentManager,
                UpdateFragment.newInstance(info)
            )
        }
    }

    fun onChannelFocused(channel: Channel) {
        currentPreviewChannel = channel

        previewName.text = channel.name
        previewGroup.text = channel.group

        val nowPlaying = EpgRepository.getNowPlaying(channel.tvgId ?: channel.name)?.title
        if (nowPlaying != null) {
            previewNowLabel.visibility = View.VISIBLE
            previewNow.visibility = View.VISIBLE
            previewNow.text = nowPlaying
        } else {
            previewNowLabel.visibility = View.GONE
            previewNow.visibility = View.GONE
        }

        val isFav = FavouritesStore.isFavourite(this, channel.url)
        previewFavBtn.text = if (isFav) getString(R.string.fav_remove) else getString(R.string.fav_add)
        previewFavBtn.visibility = View.VISIBLE
        previewHint.visibility = View.VISIBLE

        if (!channel.logoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(channel.logoUrl)
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .fitCenter()
                .into(previewLogo)
        } else {
            previewLogo.setImageResource(R.drawable.ic_channel_placeholder)
        }

        previewFavBtn.setOnClickListener {
            FavouritesStore.toggleFavourite(this, channel)
            val nowFav = FavouritesStore.isFavourite(this, channel.url)
            previewFavBtn.text = if (nowFav) getString(R.string.fav_remove) else getString(R.string.fav_add)
        }
    }

    fun onChannelClicked(channel: Channel) {
        startActivity(Intent(this, PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_URL, channel.url)
            putExtra(PlayerActivity.EXTRA_NAME, channel.name)
            putExtra(PlayerActivity.EXTRA_LOGO, channel.logoUrl)
            putExtra(PlayerActivity.EXTRA_GROUP, channel.group)
            putExtra(PlayerActivity.EXTRA_PLAYLIST_ID, channel.playlistId)
        })
    }

    fun onChannelLongClicked(channel: Channel) {
        GuidedStepSupportFragment.add(
            supportFragmentManager,
            ChannelOptionsFragment.newInstance(channel)
        )
    }

    private fun resetPreviewPanel() {
        previewFavBtn.visibility = View.GONE
        previewHint.visibility = View.GONE
        previewNowLabel.visibility = View.GONE
        previewNow.visibility = View.GONE
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val channels = supportFragmentManager
            .findFragmentById(R.id.content_container) as? ChannelsFragment

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.repeatCount == 1) {
                    if (channels?.showOptionsForFocused() == true) {
                        longPressConsumed = true
                        return true
                    }
                }
                if (longPressConsumed && event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) return true

                if (event.repeatCount == 0 && channels != null) {
                    val digit = when (event.keyCode) {
                        KeyEvent.KEYCODE_0 -> 0
                        KeyEvent.KEYCODE_1 -> 1
                        KeyEvent.KEYCODE_2 -> 2
                        KeyEvent.KEYCODE_3 -> 3
                        KeyEvent.KEYCODE_4 -> 4
                        KeyEvent.KEYCODE_5 -> 5
                        KeyEvent.KEYCODE_6 -> 6
                        KeyEvent.KEYCODE_7 -> 7
                        KeyEvent.KEYCODE_8 -> 8
                        KeyEvent.KEYCODE_9 -> 9
                        else -> null
                    }
                    if (digit != null) {
                        channels.onDigitPressed(digit)
                        return true
                    }
                }
            }
            KeyEvent.ACTION_UP -> {
                if (longPressConsumed && event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    longPressConsumed = false
                    return true
                }
            }
        }

        if (konamiDetector.onKeyDown(event.keyCode)) return true
        return super.dispatchKeyEvent(event)
    }
}
