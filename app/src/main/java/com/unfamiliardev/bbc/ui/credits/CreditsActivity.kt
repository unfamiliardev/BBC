/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.credits

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.unfamiliardev.bbc.util.LocaleHelper
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import com.unfamiliardev.bbc.BuildConfig
import com.unfamiliardev.bbc.databinding.ActivityCreditsBinding

class CreditsActivity : FragmentActivity() {

    private lateinit var binding: ActivityCreditsBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appVersion.text = "v${BuildConfig.VERSION_NAME}"

        binding.githubLink.setOnClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/unfamiliardev/BBC"))
            )
        }

        binding.dismissButton.setOnClickListener { finish() }
        binding.dismissButton.requestFocus()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
