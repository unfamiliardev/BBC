/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.content.Context

object AppSettings {

    private const val PREFS = "bbc_settings"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_AUTOPLAY = "autoplay"

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "") ?: ""

    fun setLanguage(context: Context, code: String) =
        prefs(context).edit().putString(KEY_LANGUAGE, code).apply()

    fun getAutoplay(context: Context): Boolean =
        prefs(context).getBoolean(KEY_AUTOPLAY, false)

    fun setAutoplay(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_AUTOPLAY, enabled).apply()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
