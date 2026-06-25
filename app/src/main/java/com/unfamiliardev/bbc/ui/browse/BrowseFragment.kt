/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.browse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.GuidedStepSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.ui.player.PlayerActivity
import com.unfamiliardev.bbc.ui.playlist.PlaylistActivity
import com.unfamiliardev.bbc.ui.settings.SettingsActivity
import com.unfamiliardev.bbc.util.FavouritesStore
import com.unfamiliardev.bbc.util.RecentlyWatchedStore

class BrowseFragment : BrowseSupportFragment() {

    private lateinit var viewModel: BrowseViewModel
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
    private var currentChannels: List<Channel> = emptyList()
    private var focusedChannel: Channel? = null
    private var savedRowPosition = 0

    private fun cardPresenter() = ChannelCardPresenter { channel ->
        openOptions(channel)
    }

    private fun openOptions(channel: Channel) {
        GuidedStepSupportFragment.add(
            requireActivity().supportFragmentManager,
            ChannelOptionsFragment.newInstance(channel)
        )
    }

    private fun launchPlayer(channel: Channel) {
        startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
            putExtra(PlayerActivity.EXTRA_URL, channel.url)
            putExtra(PlayerActivity.EXTRA_NAME, channel.name)
            putExtra(PlayerActivity.EXTRA_LOGO, channel.logoUrl)
            putExtra(PlayerActivity.EXTRA_GROUP, channel.group)
            putExtra(PlayerActivity.EXTRA_PLAYLIST_ID, channel.playlistId)
        })
    }

    fun showOptionsForFocused(): Boolean {
        val channel = focusedChannel ?: return false
        openOptions(channel)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)

        setOnItemViewSelectedListener { _, item, _, _ ->
            if (item is Channel) focusedChannel = item
        }

        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Channel    -> launchPlayer(item)
                is ActionItem -> when (item.id) {
                    ACTION_MANAGE      -> startActivity(Intent(requireContext(), PlaylistActivity::class.java))
                    ACTION_REFRESH     -> viewModel.refresh()
                    ACTION_SURPRISE_ME -> surpriseMe()
                    ACTION_SETTINGS    -> startActivity(Intent(requireContext(), SettingsActivity::class.java))
                    ACTION_CREDITS     -> startActivity(Intent(requireContext(), CreditsActivity::class.java))
                }
            }
        }

        setOnSearchClickedListener {
            startActivity(Intent(requireContext(), PlaylistActivity::class.java))
        }
    }

    private fun surpriseMe() {
        if (currentChannels.isEmpty()) return
        launchPlayer(currentChannels.random())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        brandColor = ContextCompat.getColor(requireContext(), R.color.brand)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.accent)
        adapter = rowsAdapter

        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]

        viewModel.channels.observe(viewLifecycleOwner) { channels ->
            currentChannels = channels
            buildRows(channels)
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) progressBarManager.show() else progressBarManager.hide()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_loading, error), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        savedRowPosition = selectedPosition
    }

    override fun onResume() {
        super.onResume()
        buildRows(currentChannels)
    }

    private fun buildRows(channels: List<Channel>) {
        rowsAdapter.clear()
        var rowId = 0L

        // Recently Watched
        val recent = RecentlyWatchedStore.get(requireContext())
        if (recent.isNotEmpty()) {
            val recentAdapter = ArrayObjectAdapter(cardPresenter())
            recentAdapter.addAll(0, recent)
            rowsAdapter.add(ListRow(HeaderItem(rowId++, getString(R.string.category_recent)), recentAdapter))
        }

        // Favourites
        val favs = FavouritesStore.getFavourites(requireContext())
        if (favs.isNotEmpty()) {
            val favsAdapter = ArrayObjectAdapter(cardPresenter())
            favsAdapter.addAll(0, favs)
            rowsAdapter.add(ListRow(HeaderItem(rowId++, getString(R.string.category_favourites)), favsAdapter))
        }

        // My List
        val myList = FavouritesStore.getMyList(requireContext())
        if (myList.isNotEmpty()) {
            val myListAdapter = ArrayObjectAdapter(cardPresenter())
            myListAdapter.addAll(0, myList)
            rowsAdapter.add(ListRow(HeaderItem(rowId++, getString(R.string.category_my_list)), myListAdapter))
        }

        if (channels.isEmpty()) {
            val emptyAdapter = ArrayObjectAdapter(ActionPresenter())
            emptyAdapter.add(ActionItem(ACTION_MANAGE, getString(R.string.add_first_playlist)))
            rowsAdapter.add(ListRow(HeaderItem(rowId++, getString(R.string.getting_started)), emptyAdapter))
        } else {
            val allAdapter = ArrayObjectAdapter(cardPresenter())
            allAdapter.addAll(0, channels)
            rowsAdapter.add(
                ListRow(HeaderItem(rowId++, getString(R.string.category_all, channels.size)), allAdapter)
            )

            channels.groupBy { it.group }
                .entries
                .sortedBy { it.key }
                .forEach { (group, grouped) ->
                    val adapter = ArrayObjectAdapter(cardPresenter())
                    adapter.addAll(0, grouped)
                    rowsAdapter.add(ListRow(HeaderItem(rowId++, "$group (${grouped.size})"), adapter))
                }
        }

        // Actions row
        val actionsAdapter = ArrayObjectAdapter(ActionPresenter())
        actionsAdapter.add(ActionItem(ACTION_MANAGE,      getString(R.string.manage_playlists_action)))
        actionsAdapter.add(ActionItem(ACTION_REFRESH,     getString(R.string.refresh)))
        if (currentChannels.isNotEmpty()) {
            actionsAdapter.add(ActionItem(ACTION_SURPRISE_ME, getString(R.string.surprise_me)))
        }
        actionsAdapter.add(ActionItem(ACTION_SETTINGS,    getString(R.string.settings)))
        actionsAdapter.add(ActionItem(ACTION_CREDITS,     getString(R.string.credits)))
        rowsAdapter.add(ListRow(HeaderItem(rowId, getString(R.string.settings)), actionsAdapter))

        val target = savedRowPosition.coerceAtMost(rowsAdapter.size() - 1).coerceAtLeast(0)
        view?.post { setSelectedPosition(target, false) }
    }

    companion object {
        private const val ACTION_MANAGE      = "action_manage"
        private const val ACTION_REFRESH     = "action_refresh"
        private const val ACTION_SURPRISE_ME = "action_surprise"
        private const val ACTION_SETTINGS    = "action_settings"
        private const val ACTION_CREDITS     = "action_credits"
    }
}
