/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun onAttach(context: Context): Context {
        val lang = AppSettings.getLanguage(context)
        return if (lang.isEmpty()) context else applyLocale(context, lang)
    }

    fun setLocale(context: Context, languageCode: String) {
        AppSettings.setLanguage(context, languageCode)
    }

    private fun applyLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
