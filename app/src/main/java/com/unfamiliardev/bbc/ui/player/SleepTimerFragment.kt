/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.player

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

class SleepTimerFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            "Sleep Timer",
            "Playback will automatically stop after the selected time.",
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val ctx = requireContext()
        listOf(
            "Off"          to 0,
            "15 minutes"   to 15,
            "30 minutes"   to 30,
            "60 minutes"   to 60,
            "90 minutes"   to 90,
            "2 hours"      to 120,
        ).forEach { (label, minutes) ->
            actions += GuidedAction.Builder(ctx).id(minutes.toLong()).title(label).build()
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        (activity as? PlayerActivity)?.setSleepTimer(action.id.toInt())
        val count = parentFragmentManager.backStackEntryCount
        repeat(count) { parentFragmentManager.popBackStack() }
    }
}
