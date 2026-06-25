/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc

import android.app.Application
import android.content.Context
import com.unfamiliardev.bbc.data.DefaultPlaylist
import com.unfamiliardev.bbc.data.db.AppDatabase
import com.unfamiliardev.bbc.data.db.PlaylistEntity
import com.unfamiliardev.bbc.util.LocaleHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BBCApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        seedDefaultPlaylist()
    }

    private fun seedDefaultPlaylist() {
        val prefs = getSharedPreferences("bbc_init", Context.MODE_PRIVATE)
        if (prefs.getBoolean("default_seeded", false)) return

        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getInstance(this@BBCApplication).playlistDao()
            if (dao.getAllOnce().isEmpty()) {
                dao.insert(
                    PlaylistEntity(
                        name = DefaultPlaylist.BUILTIN_NAME,
                        url = DefaultPlaylist.BUILTIN_URL
                    )
                )
            }
            prefs.edit().putBoolean("default_seeded", true).apply()
        }
    }
}
