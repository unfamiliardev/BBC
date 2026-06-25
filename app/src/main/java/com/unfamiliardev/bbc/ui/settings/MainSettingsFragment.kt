/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import com.unfamiliardev.bbc.BuildConfig
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.repository.PlaylistRepository
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.ui.playlist.PlaylistActivity
import com.unfamiliardev.bbc.util.AppSettings
import kotlinx.coroutines.launch

class MainSettingsFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            getString(R.string.settings_title),
            getString(R.string.settings_desc),
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_SOURCES)
                .title(getString(R.string.setting_sources))
                .description(getString(R.string.setting_sources_desc))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_LANGUAGE)
                .title(getString(R.string.setting_language))
                .description(currentLanguageLabel())
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_AUTOPLAY)
                .title(getString(R.string.setting_autoplay))
                .description(autoplayLabel())
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_CLEAR)
                .title(getString(R.string.setting_clear_data))
                .description(getString(R.string.setting_clear_data_desc))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_EPG)
                .title(getString(R.string.setting_epg_source))
                .description(AppSettings.getEpgUrl(requireContext()).ifEmpty { getString(R.string.setting_epg_source_desc) })
                .descriptionEditable(true)
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_ABOUT)
                .title(getString(R.string.setting_about))
                .description("v${BuildConfig.VERSION_NAME}")
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_SOURCES -> startActivity(Intent(requireContext(), PlaylistActivity::class.java))

            ACTION_LANGUAGE -> add(parentFragmentManager, LanguagePickerFragment())

            ACTION_AUTOPLAY -> {
                val newVal = !AppSettings.getAutoplay(requireContext())
                AppSettings.setAutoplay(requireContext(), newVal)
                action.description = autoplayLabel()
                notifyActionChanged(findActionPositionById(ACTION_AUTOPLAY))
            }

            ACTION_CLEAR -> {
                lifecycleScope.launch {
                    PlaylistRepository(requireContext()).clearAll()
                }
                Toast.makeText(requireContext(), R.string.confirm_clear, Toast.LENGTH_SHORT).show()
            }

            ACTION_EPG -> {
                val url = action.description?.toString() ?: ""
                AppSettings.setEpgUrl(requireContext(), url)
            }

            ACTION_ABOUT -> startActivity(Intent(requireContext(), CreditsActivity::class.java))
        }
    }

    private fun currentLanguageLabel() = when (AppSettings.getLanguage(requireContext())) {
        "en" -> getString(R.string.lang_en)
        "ru" -> getString(R.string.lang_ru)
        "sk" -> getString(R.string.lang_sk)
        "de" -> getString(R.string.lang_de)
        else -> getString(R.string.lang_system)
    }

    private fun autoplayLabel() =
        if (AppSettings.getAutoplay(requireContext())) getString(R.string.autoplay_enabled)
        else getString(R.string.autoplay_disabled)

    companion object {
        private const val ACTION_SOURCES  = 0L
        private const val ACTION_LANGUAGE = 1L
        private const val ACTION_AUTOPLAY = 2L
        private const val ACTION_CLEAR = 3L
        private const val ACTION_EPG = 4L
        private const val ACTION_ABOUT = 5L
    }
}
