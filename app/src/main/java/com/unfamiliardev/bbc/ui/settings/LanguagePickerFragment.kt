/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.ui.MainActivity
import com.unfamiliardev.bbc.util.AppSettings
import com.unfamiliardev.bbc.util.LocaleHelper

class LanguagePickerFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            getString(R.string.setting_language),
            getString(R.string.setting_language_desc),
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val current = AppSettings.getLanguage(requireContext())
        LANGUAGES.forEach { (id, code, labelRes) ->
            actions.add(
                GuidedAction.Builder(requireContext())
                    .id(id)
                    .title(getString(labelRes))
                    .checkSetId(CHECK_SET_ID)
                    .checked(code == current)
                    .build()
            )
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val code = LANGUAGES.firstOrNull { it.first == action.id }?.second ?: return
        LocaleHelper.setLocale(requireContext(), code)
        // Restart the app cleanly so the new locale takes effect everywhere
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    companion object {
        private const val CHECK_SET_ID = 100

        val LANGUAGES = listOf(
            Triple(0L, "",   R.string.lang_system),
            Triple(1L, "en", R.string.lang_en),
            Triple(2L, "ru", R.string.lang_ru),
            Triple(3L, "sk", R.string.lang_sk),
            Triple(4L, "de", R.string.lang_de),
        )
    }
}
