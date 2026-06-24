/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.browse

import android.os.Bundle
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.GuidanceStylist
import androidx.leanback.widget.GuidedAction
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.util.FavouritesStore

class ChannelOptionsFragment : GuidedStepSupportFragment() {

    private val channel: Channel get() = Channel(
        id        = requireArguments().getString(ARG_URL, ""),
        name      = requireArguments().getString(ARG_NAME, ""),
        url       = requireArguments().getString(ARG_URL, ""),
        logoUrl   = requireArguments().getString(ARG_LOGO, "").ifEmpty { null },
        group     = requireArguments().getString(ARG_GROUP, ""),
        playlistId = requireArguments().getLong(ARG_PLAYLIST_ID, -1L)
    )

    override fun onCreateGuidance(savedInstanceState: Bundle?): GuidanceStylist.Guidance =
        GuidanceStylist.Guidance(
            requireArguments().getString(ARG_NAME, ""),
            requireArguments().getString(ARG_GROUP, ""),
            "",
            null
        )

    override fun onCreateActions(actions: MutableList<GuidedAction>, savedInstanceState: Bundle?) {
        val url = requireArguments().getString(ARG_URL, "")
        val isFav = FavouritesStore.isFavourite(requireContext(), url)
        val inList = FavouritesStore.isInMyList(requireContext(), url)

        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_FAV)
                .title(getString(if (isFav) R.string.remove_favourite else R.string.add_favourite))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_MY_LIST)
                .title(getString(if (inList) R.string.remove_my_list else R.string.add_my_list))
                .build()
        )
        actions.add(
            GuidedAction.Builder(requireContext())
                .id(ACTION_CANCEL)
                .title(getString(android.R.string.cancel))
                .build()
        )
    }

    override fun onGuidedActionClicked(action: GuidedAction) {
        when (action.id) {
            ACTION_FAV     -> FavouritesStore.toggleFavourite(requireContext(), channel)
            ACTION_MY_LIST -> FavouritesStore.toggleMyList(requireContext(), channel)
        }
        parentFragmentManager.popBackStack()
    }

    companion object {
        private const val ARG_NAME        = "name"
        private const val ARG_URL         = "url"
        private const val ARG_LOGO        = "logo"
        private const val ARG_GROUP       = "group"
        private const val ARG_PLAYLIST_ID = "playlist_id"

        private const val ACTION_FAV     = 1L
        private const val ACTION_MY_LIST = 2L
        private const val ACTION_CANCEL  = 3L

        fun newInstance(channel: Channel) = ChannelOptionsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_NAME,  channel.name)
                putString(ARG_URL,   channel.url)
                putString(ARG_LOGO,  channel.logoUrl ?: "")
                putString(ARG_GROUP, channel.group)
                putLong(ARG_PLAYLIST_ID, channel.playlistId)
            }
        }
    }
}
