/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.lifecycle.lifecycleScope
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.ui.browse.BrowseFragment
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.ui.player.PlayerActivity
import com.unfamiliardev.bbc.ui.update.UpdateFragment
import com.unfamiliardev.bbc.util.AppSettings
import com.unfamiliardev.bbc.util.KonamiCodeDetector
import com.unfamiliardev.bbc.util.LocaleHelper
import com.unfamiliardev.bbc.util.UpdateChecker
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    private var longPressConsumed = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, BrowseFragment())
                .commit()

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

    private fun checkForUpdate() {
        lifecycleScope.launch {
            val info = UpdateChecker.check(this@MainActivity) ?: return@launch
            GuidedStepSupportFragment.add(
                supportFragmentManager,
                UpdateFragment.newInstance(info)
            )
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val browse = supportFragmentManager.findFragmentById(R.id.main_container) as? BrowseFragment

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER && event.repeatCount == 1) {
                    if (browse?.showOptionsForFocused() == true) {
                        longPressConsumed = true
                        return true
                    }
                }
                if (longPressConsumed && event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) return true
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
