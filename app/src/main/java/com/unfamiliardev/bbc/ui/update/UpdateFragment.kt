/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.update

import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import androidx.lifecycle.lifecycleScope
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.util.UpdateChecker
import kotlinx.coroutines.launch

class UpdateFragment : GuidedStepSupportFragment() {

    private val versionName get() = requireArguments().getString(ARG_VERSION, "")
    private val versionCode get() = requireArguments().getInt(ARG_CODE, 0)
    private val apkUrl     get() = requireArguments().getString(ARG_URL, "")
    private val notes      get() = requireArguments().getString(ARG_NOTES, "")

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            getString(R.string.update_title),
            "${getString(R.string.update_desc, versionName)}\n\n$notes".trim(),
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_INSTALL)
                .title(getString(R.string.update_install_now))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_LATER)
                .title(getString(R.string.update_later))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_SKIP)
                .title(getString(R.string.update_skip_version))
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_INSTALL -> downloadAndInstall()
            ACTION_LATER   -> parentFragmentManager.popBackStack()
            ACTION_SKIP    -> {
                UpdateChecker.skipVersion(requireContext(), versionCode)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun downloadAndInstall() {
        // Disable actions during download
        val installAction = findActionById(ACTION_INSTALL)
        installAction?.title = getString(R.string.update_downloading)
        notifyActionChanged(findActionPositionById(ACTION_INSTALL))

        lifecycleScope.launch {
            val file = UpdateChecker.download(requireContext(), apkUrl) { progress ->
                requireActivity().runOnUiThread {
                    installAction?.title = getString(R.string.update_downloading_progress, progress)
                    notifyActionChanged(findActionPositionById(ACTION_INSTALL))
                }
            }

            if (file != null) {
                UpdateChecker.install(requireContext(), file)
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), getString(R.string.update_download_failed), Toast.LENGTH_LONG).show()
                installAction?.title = getString(R.string.update_install_now)
                notifyActionChanged(findActionPositionById(ACTION_INSTALL))
            }
        }
    }

    companion object {
        private const val ARG_VERSION = "version"
        private const val ARG_CODE    = "code"
        private const val ARG_URL     = "url"
        private const val ARG_NOTES   = "notes"

        private const val ACTION_INSTALL = 1L
        private const val ACTION_LATER   = 2L
        private const val ACTION_SKIP    = 3L

        fun newInstance(info: UpdateChecker.UpdateInfo) = UpdateFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_VERSION, info.versionName)
                putInt(ARG_CODE, info.versionCode)
                putString(ARG_URL, info.apkUrl)
                putString(ARG_NOTES, info.releaseNotes)
            }
        }
    }
}
