/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.channels

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.ui.MainActivity
import com.unfamiliardev.bbc.ui.browse.BrowseViewModel
import com.unfamiliardev.bbc.util.FavouritesStore
import com.unfamiliardev.bbc.util.RecentlyWatchedStore

class ChannelsFragment : Fragment() {

    private lateinit var viewModel: BrowseViewModel
    private lateinit var adapter: ChannelListAdapter

    private var allChannels: List<Channel> = emptyList()
    private var selectedCategory = "ALL"
    private var focusedChannel: Channel? = null

    private val recentOnly: Boolean get() = arguments?.getBoolean(ARG_RECENT_ONLY, false) == true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_channels, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val channelList = view.findViewById<RecyclerView>(R.id.channel_list)
        val categoryTabs = view.findViewById<LinearLayout>(R.id.category_tabs)
        val searchBar = view.findViewById<EditText>(R.id.search_bar)
        val titleView = view.findViewById<TextView>(R.id.channels_title)
        val countView = view.findViewById<TextView>(R.id.channels_count)

        adapter = ChannelListAdapter(
            onFocused = { channel ->
                focusedChannel = channel
                (activity as? MainActivity)?.onChannelFocused(channel)
            },
            onClicked = { channel ->
                (activity as? MainActivity)?.onChannelClicked(channel)
            },
            onLongClicked = { channel ->
                (activity as? MainActivity)?.onChannelLongClicked(channel)
            }
        )

        channelList.adapter = adapter
        channelList.layoutManager = LinearLayoutManager(requireContext())
        channelList.setHasFixedSize(true)

        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]

        viewModel.channels.observe(viewLifecycleOwner) { channels ->
            allChannels = channels

            if (recentOnly) {
                val recent = RecentlyWatchedStore.get(requireContext())
                titleView.text = getString(R.string.category_recent)
                countView.text = "${recent.size} channels"
                adapter.updateChannels(recent)
                categoryTabs.removeAllViews()
            } else {
                titleView.text = getString(R.string.live_tv)
                countView.text = "ALL (${channels.size} channels)"
                buildCategoryTabs(categoryTabs, channels)
                applyFilter(searchBar.text.toString())
            }
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                applyFilter(s?.toString() ?: "")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!recentOnly) {
            applyFilter("")
        }
    }

    private fun buildCategoryTabs(container: LinearLayout, channels: List<Channel>) {
        container.removeAllViews()
        val groups = listOf("ALL") + channels.map { it.group }.distinct().sorted()
        val density = resources.displayMetrics.density
        val hPad = (20 * density).toInt()
        val vPad = (6 * density).toInt()
        val marginPx = (8 * density).toInt()

        groups.forEach { group ->
            val tab = TextView(requireContext()).apply {
                text = group
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_text))
                setPadding(hPad, vPad, hPad, vPad)
                gravity = Gravity.CENTER
                isFocusable = true
                minWidth = (60 * density).toInt()
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                lp.setMargins(0, (8 * density).toInt(), marginPx, (8 * density).toInt())
                layoutParams = lp
                applyTabStyle(this, group == selectedCategory)
                setOnClickListener { selectCategory(group) }
                setOnFocusChangeListener { _, focused -> if (focused) selectCategory(group) }
            }
            container.addView(tab)
        }
    }

    private fun selectCategory(category: String) {
        selectedCategory = category
        val container = view?.findViewById<LinearLayout>(R.id.category_tabs) ?: return
        for (i in 0 until container.childCount) {
            val child = container.getChildAt(i) as? TextView ?: continue
            applyTabStyle(child, child.text == category)
        }
        applyFilter(view?.findViewById<EditText>(R.id.search_bar)?.text?.toString() ?: "")
    }

    private fun applyTabStyle(tab: TextView, selected: Boolean) {
        val density = resources.displayMetrics.density
        val radius = (16 * density)
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = radius
            if (selected) {
                setColor(ContextCompat.getColor(requireContext(), R.color.accent))
            } else {
                setColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                setStroke(
                    (1 * density).toInt(),
                    ContextCompat.getColor(requireContext(), R.color.divider)
                )
            }
        }
        tab.background = bg
    }

    private fun applyFilter(query: String) {
        val base = if (selectedCategory == "ALL") allChannels
                   else allChannels.filter { it.group == selectedCategory }
        val result = if (query.isBlank()) base
                     else base.filter { it.name.contains(query, ignoreCase = true) }

        val favs = FavouritesStore.getFavourites(requireContext()).map { it.url }.toSet()
        val sorted = result.sortedWith(
            compareByDescending<Channel> { it.url in favs }.thenBy { it.name }
        )
        adapter.updateChannels(sorted)
    }

    fun showOptionsForFocused(): Boolean {
        val channel = focusedChannel ?: return false
        (activity as? MainActivity)?.onChannelLongClicked(channel)
        return true
    }

    companion object {
        private const val ARG_RECENT_ONLY = "recent_only"

        fun newInstance(recentOnly: Boolean) = ChannelsFragment().apply {
            arguments = bundleOf(ARG_RECENT_ONLY to recentOnly)
        }
    }
}
