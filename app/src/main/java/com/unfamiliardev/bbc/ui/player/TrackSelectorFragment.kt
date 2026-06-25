/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.player

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

data class TrackInfo(val groupIndex: Int, val trackIndex: Int, val label: String)

class TrackSelectorFragment : GuidedStepSupportFragment() {

    private val trackType: String get() = arguments?.getString(ARG_TYPE, "audio") ?: "audio"

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            if (trackType == "audio") "Audio Track" else "Subtitles",
            "Choose a track",
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val ctx = requireContext()
        if (trackType == "subtitle") {
            actions += GuidedAction.Builder(ctx).id(ID_OFF).title("Off").build()
        }
        tracks.forEachIndexed { index, track ->
            actions += GuidedAction.Builder(ctx).id(index.toLong()).title(track.label).build()
        }
        if (tracks.isEmpty()) {
            actions += GuidedAction.Builder(ctx).id(ID_NONE).title("No tracks available").build()
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ID_OFF  -> onTrackSelected?.invoke(-1, -1)
            ID_NONE -> { /* nothing */ }
            else    -> tracks.getOrNull(action.id.toInt())?.let { track ->
                onTrackSelected?.invoke(track.groupIndex, track.trackIndex)
            }
        }
        val count = parentFragmentManager.backStackEntryCount
        repeat(count) { parentFragmentManager.popBackStack() }
    }

    companion object {
        private const val ARG_TYPE = "track_type"
        private const val ID_OFF   = -1L
        private const val ID_NONE  = -2L

        var tracks: List<TrackInfo> = emptyList()
        var onTrackSelected: ((groupIndex: Int, trackIndex: Int) -> Unit)? = null

        fun newInstance(type: String) = TrackSelectorFragment().apply {
            arguments = bundleOf(ARG_TYPE to type)
        }
    }
}
