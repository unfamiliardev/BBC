package com.unfamiliardev.bbc.ui.player

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

class PlayerMenuFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance("Player Options", "Select an option", "", null)

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val ctx = requireContext()
        actions += GuidedAction.Builder(ctx).id(ACTION_SLEEP).title("😴  Sleep Timer").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_AUDIO).title("🎵  Audio Track").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_SUBS).title("💬  Subtitles").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_ASPECT).title("📐  Aspect Ratio").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_INFO).title("📊  Stream Info").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_CLOSE).title("✕   Close").build()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val player = activity as? PlayerActivity ?: return
        when (action.id) {
            ACTION_SLEEP  -> add(parentFragmentManager, SleepTimerFragment())
            ACTION_AUDIO  -> { player.openTrackSelector("audio"); dismiss() }
            ACTION_SUBS   -> { player.openTrackSelector("subtitle"); dismiss() }
            ACTION_ASPECT -> { player.cycleAspectRatio(); dismiss() }
            ACTION_INFO   -> { player.toggleStreamInfo(); dismiss() }
            ACTION_CLOSE  -> dismiss()
        }
    }

    private fun dismiss() {
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val ACTION_SLEEP  = 1L
        private const val ACTION_AUDIO  = 2L
        private const val ACTION_SUBS   = 3L
        private const val ACTION_ASPECT = 4L
        private const val ACTION_INFO   = 5L
        private const val ACTION_CLOSE  = 6L
    }
}
