package com.unfamiliardev.bbc.util

import android.content.Context

object AppSettings {

    private const val PREFS = "bbc_settings"
    private const val KEY_LANGUAGE = "language"
    private const val KEY_AUTOPLAY = "autoplay"
    private const val KEY_EPG_URL = "epg_url"
    private const val KEY_STARTUP_TAB = "startup_tab"
    private const val KEY_PLAYER_QUALITY = "player_quality"
    private const val KEY_SHOW_STREAM_INFO = "show_stream_info_on_start"

    const val TAB_LIVE = "live"
    const val TAB_VOD = "vod"
    const val TAB_RECENT = "recent"
    const val TAB_EPG = "epg"

    const val QUALITY_AUTO = "auto"
    const val QUALITY_BEST = "best"
    const val QUALITY_LOW = "low"

    fun getLanguage(context: Context): String =
        prefs(context).getString(KEY_LANGUAGE, "") ?: ""

    fun setLanguage(context: Context, code: String) =
        prefs(context).edit().putString(KEY_LANGUAGE, code).apply()

    fun getAutoplay(context: Context): Boolean =
        prefs(context).getBoolean(KEY_AUTOPLAY, false)

    fun setAutoplay(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_AUTOPLAY, enabled).apply()

    fun getEpgUrl(context: Context): String =
        prefs(context).getString(KEY_EPG_URL, "") ?: ""

    fun setEpgUrl(context: Context, url: String) =
        prefs(context).edit().putString(KEY_EPG_URL, url).apply()

    fun getStartupTab(context: Context): String =
        prefs(context).getString(KEY_STARTUP_TAB, TAB_LIVE) ?: TAB_LIVE

    fun setStartupTab(context: Context, tab: String) =
        prefs(context).edit().putString(KEY_STARTUP_TAB, tab).apply()

    fun getPlayerQuality(context: Context): String =
        prefs(context).getString(KEY_PLAYER_QUALITY, QUALITY_AUTO) ?: QUALITY_AUTO

    fun setPlayerQuality(context: Context, quality: String) =
        prefs(context).edit().putString(KEY_PLAYER_QUALITY, quality).apply()

    fun getShowStreamInfoOnStart(context: Context): Boolean =
        prefs(context).getBoolean(KEY_SHOW_STREAM_INFO, false)

    fun setShowStreamInfoOnStart(context: Context, enabled: Boolean) =
        prefs(context).edit().putBoolean(KEY_SHOW_STREAM_INFO, enabled).apply()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
