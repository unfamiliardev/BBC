/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.browse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.ViewModelProvider
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.ui.player.PlayerActivity
import com.unfamiliardev.bbc.ui.playlist.PlaylistActivity
import com.unfamiliardev.bbc.ui.settings.SettingsActivity

class BrowseFragment : BrowseSupportFragment() {

    private lateinit var viewModel: BrowseViewModel
    private val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true
        title = getString(R.string.app_name)

        setOnItemViewClickedListener { _, item, _, _ ->
            when (item) {
                is Channel -> startActivity(
                    Intent(requireContext(), PlayerActivity::class.java).apply {
                        putExtra(PlayerActivity.EXTRA_URL, item.url)
                        putExtra(PlayerActivity.EXTRA_NAME, item.name)
                    }
                )
                is ActionItem -> when (item.id) {
                    ACTION_MANAGE   -> startActivity(Intent(requireContext(), PlaylistActivity::class.java))
                    ACTION_REFRESH  -> viewModel.refresh()
                    ACTION_SETTINGS -> startActivity(Intent(requireContext(), SettingsActivity::class.java))
                    ACTION_CREDITS  -> startActivity(Intent(requireContext(), CreditsActivity::class.java))
                }
            }
        }

        setOnSearchClickedListener {
            startActivity(Intent(requireContext(), PlaylistActivity::class.java))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        brandColor = ContextCompat.getColor(requireContext(), R.color.brand)
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.accent)
        adapter = rowsAdapter

        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]

        viewModel.channels.observe(viewLifecycleOwner) { channels -> buildRows(channels) }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) progressBarManager.show() else progressBarManager.hide()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.error_loading, error), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun buildRows(channels: List<Channel>) {
        rowsAdapter.clear()

        if (channels.isEmpty()) {
            val emptyAdapter = ArrayObjectAdapter(ActionPresenter())
            emptyAdapter.add(ActionItem(ACTION_MANAGE, getString(R.string.add_first_playlist)))
            rowsAdapter.add(ListRow(HeaderItem(0, getString(R.string.getting_started)), emptyAdapter))
        } else {
            val allAdapter = ArrayObjectAdapter(ChannelCardPresenter())
            allAdapter.addAll(0, channels)
            rowsAdapter.add(
                ListRow(HeaderItem(0, getString(R.string.category_all, channels.size)), allAdapter)
            )
            channels.groupBy { it.group }
                .entries
                .sortedBy { it.key }
                .forEachIndexed { idx, (group, grouped) ->
                    val adapter = ArrayObjectAdapter(ChannelCardPresenter())
                    adapter.addAll(0, grouped)
                    rowsAdapter.add(
                        ListRow(HeaderItem((idx + 1).toLong(), "$group (${grouped.size})"), adapter)
                    )
                }
        }

        val actionsAdapter = ArrayObjectAdapter(ActionPresenter())
        actionsAdapter.add(ActionItem(ACTION_MANAGE,   getString(R.string.manage_playlists_action)))
        actionsAdapter.add(ActionItem(ACTION_REFRESH,  getString(R.string.refresh)))
        actionsAdapter.add(ActionItem(ACTION_SETTINGS, getString(R.string.settings)))
        actionsAdapter.add(ActionItem(ACTION_CREDITS,  getString(R.string.credits)))
        rowsAdapter.add(
            ListRow(HeaderItem(rowsAdapter.size().toLong(), getString(R.string.settings)), actionsAdapter)
        )
    }

    companion object {
        private const val ACTION_MANAGE   = "action_manage"
        private const val ACTION_REFRESH  = "action_refresh"
        private const val ACTION_SETTINGS = "action_settings"
        private const val ACTION_CREDITS  = "action_credits"
    }
}
