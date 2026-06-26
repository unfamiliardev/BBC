package com.unfamiliardev.bbc.ui.player

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction

class PlayerMenuFragment : GuidedStepSupportFragment() {

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance("Player", "", "", null)

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val ctx = requireContext()
        val player = activity as? PlayerActivity
        val muteLabel = if (player?.isMuted == true) "Unmute" else "Mute"
        val seekable = player?.isCurrentStreamSeekable() == true

        actions += GuidedAction.Builder(ctx).id(ACTION_SLEEP).title("Sleep Timer").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_AUDIO).title("Audio Track").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_SUBS).title("Subtitles").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_ASPECT).title("Aspect Ratio").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_MUTE).title(muteLabel).build()
        if (seekable) {
            actions += GuidedAction.Builder(ctx).id(ACTION_SPEED).title("Playback Speed").build()
        }
        actions += GuidedAction.Builder(ctx).id(ACTION_RELOAD).title("Reload Stream").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_COPY).title("Copy Stream URL").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_INFO).title("Stream Info").build()
        actions += GuidedAction.Builder(ctx).id(ACTION_CLOSE).title("Close").build()
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        val player = activity as? PlayerActivity ?: return
        when (action.id) {
            ACTION_SLEEP   -> add(parentFragmentManager, SleepTimerFragment())
            ACTION_AUDIO   -> { player.openTrackSelector("audio"); dismiss() }
            ACTION_SUBS    -> { player.openTrackSelector("subtitle"); dismiss() }
            ACTION_ASPECT  -> { player.cycleAspectRatio(); dismiss() }
            ACTION_MUTE    -> { player.toggleMute(); dismiss() }
            ACTION_SPEED   -> add(parentFragmentManager, SpeedFragment())
            ACTION_RELOAD  -> { player.reloadStream(); dismiss() }
            ACTION_COPY    -> {
                val cm = requireContext().getSystemService(ClipboardManager::class.java)
                cm.setPrimaryClip(ClipData.newPlainText("stream_url", player.currentUrl))
                Toast.makeText(requireContext(), "URL copied", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            ACTION_INFO    -> { player.toggleStreamInfo(); dismiss() }
            ACTION_CLOSE   -> dismiss()
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
        private const val ACTION_MUTE   = 7L
        private const val ACTION_SPEED  = 8L
        private const val ACTION_RELOAD = 9L
        private const val ACTION_COPY   = 10L
    }
}
