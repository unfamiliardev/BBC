package com.unfamiliardev.bbc.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.BuildConfig
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.repository.EpgRepository
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
        val ctx = requireContext()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_SOURCES)
            .title(getString(R.string.setting_sources))
            .description(getString(R.string.setting_sources_desc))
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_EPG)
            .title(getString(R.string.setting_epg_source))
            .description(AppSettings.getEpgUrl(ctx).ifEmpty { getString(R.string.setting_epg_source_desc) })
            .descriptionEditable(true)
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_STARTUP_TAB)
            .title(getString(R.string.setting_startup_tab))
            .description(startupTabLabel())
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_QUALITY)
            .title(getString(R.string.setting_quality))
            .description(qualityLabel())
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_AUTOPLAY)
            .title(getString(R.string.setting_autoplay))
            .description(autoplayLabel())
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_LANGUAGE)
            .title(getString(R.string.setting_language))
            .description(currentLanguageLabel())
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_CLEAR_CACHE)
            .title(getString(R.string.setting_clear_cache))
            .description(getString(R.string.setting_clear_cache_desc))
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_CLEAR)
            .title(getString(R.string.setting_clear_data))
            .description(getString(R.string.setting_clear_data_desc))
            .build()

        actions += GuidedAction.Builder(ctx)
            .id(ACTION_ABOUT)
            .title(getString(R.string.setting_about))
            .description("v${BuildConfig.VERSION_NAME}")
            .build()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val ctx = requireContext()
        when (action.id) {
            ACTION_SOURCES -> startActivity(Intent(ctx, PlaylistActivity::class.java))

            ACTION_EPG -> {
                val url = action.description?.toString() ?: ""
                AppSettings.setEpgUrl(ctx, url)
            }

            ACTION_STARTUP_TAB -> {
                val tabs = listOf(
                    AppSettings.TAB_LIVE to getString(R.string.live_tv),
                    AppSettings.TAB_VOD to getString(R.string.vod_title),
                    AppSettings.TAB_RECENT to getString(R.string.category_recent),
                    AppSettings.TAB_EPG to getString(R.string.tv_guide)
                )
                val current = AppSettings.getStartupTab(ctx)
                val next = tabs[(tabs.indexOfFirst { it.first == current } + 1) % tabs.size]
                AppSettings.setStartupTab(ctx, next.first)
                action.description = next.second
                notifyActionChanged(findActionPositionById(ACTION_STARTUP_TAB))
            }

            ACTION_QUALITY -> {
                val qualities = listOf(
                    AppSettings.QUALITY_AUTO to getString(R.string.quality_auto),
                    AppSettings.QUALITY_BEST to getString(R.string.quality_best),
                    AppSettings.QUALITY_LOW to getString(R.string.quality_low)
                )
                val current = AppSettings.getPlayerQuality(ctx)
                val next = qualities[(qualities.indexOfFirst { it.first == current } + 1) % qualities.size]
                AppSettings.setPlayerQuality(ctx, next.first)
                action.description = next.second
                notifyActionChanged(findActionPositionById(ACTION_QUALITY))
            }

            ACTION_AUTOPLAY -> {
                val newVal = !AppSettings.getAutoplay(ctx)
                AppSettings.setAutoplay(ctx, newVal)
                action.description = autoplayLabel()
                notifyActionChanged(findActionPositionById(ACTION_AUTOPLAY))
            }

            ACTION_LANGUAGE -> add(parentFragmentManager, LanguagePickerFragment())

            ACTION_CLEAR_CACHE -> {
                EpgRepository.clearCache()
                Glide.get(ctx).clearMemory()
                lifecycleScope.launch { Glide.get(ctx).clearDiskCache() }
                Toast.makeText(ctx, R.string.cache_cleared, Toast.LENGTH_SHORT).show()
            }

            ACTION_CLEAR -> {
                lifecycleScope.launch { PlaylistRepository(ctx).clearAll() }
                Toast.makeText(ctx, R.string.confirm_clear, Toast.LENGTH_SHORT).show()
            }

            ACTION_ABOUT -> startActivity(Intent(ctx, CreditsActivity::class.java))
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

    private fun startupTabLabel() = when (AppSettings.getStartupTab(requireContext())) {
        AppSettings.TAB_VOD -> getString(R.string.vod_title)
        AppSettings.TAB_RECENT -> getString(R.string.category_recent)
        AppSettings.TAB_EPG -> getString(R.string.tv_guide)
        else -> getString(R.string.live_tv)
    }

    private fun qualityLabel() = when (AppSettings.getPlayerQuality(requireContext())) {
        AppSettings.QUALITY_BEST -> getString(R.string.quality_best)
        AppSettings.QUALITY_LOW -> getString(R.string.quality_low)
        else -> getString(R.string.quality_auto)
    }

    companion object {
        private const val ACTION_SOURCES       = 0L
        private const val ACTION_EPG           = 1L
        private const val ACTION_STARTUP_TAB   = 2L
        private const val ACTION_QUALITY       = 3L
        private const val ACTION_AUTOPLAY      = 4L
        private const val ACTION_LANGUAGE      = 5L
        private const val ACTION_CLEAR_CACHE   = 6L
        private const val ACTION_CLEAR         = 7L
        private const val ACTION_ABOUT         = 8L
    }
}
