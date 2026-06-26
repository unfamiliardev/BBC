package com.unfamiliardev.bbc.ui.player

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

class SpeedFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance("Playback Speed", "", "", null)

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val ctx = requireContext()
        val current = (activity as? PlayerActivity)?.currentPlaybackSpeed ?: 1f
        listOf(
            "0.5x"  to 0.5f,
            "0.75x" to 0.75f,
            "1x"    to 1f,
            "1.25x" to 1.25f,
            "1.5x"  to 1.5f,
            "2x"    to 2f,
        ).forEach { (label, speed) ->
            val title = if (speed == current) "$label  (current)" else label
            actions += GuidedAction.Builder(ctx)
                .id((speed * 100).toLong())
                .title(title)
                .build()
        }
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val speed = action.id / 100f
        (activity as? PlayerActivity)?.setPlaybackSpeed(speed)
        val count = parentFragmentManager.backStackEntryCount
        repeat(count) { parentFragmentManager.popBackStack() }
    }
}
