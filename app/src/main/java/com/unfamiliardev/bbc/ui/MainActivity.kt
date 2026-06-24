/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.ui.browse.BrowseFragment
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.util.KonamiCodeDetector

class MainActivity : FragmentActivity() {

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, BrowseFragment())
                .commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (konamiDetector.onKeyDown(keyCode)) return true
        return super.onKeyDown(keyCode, event)
    }
}
