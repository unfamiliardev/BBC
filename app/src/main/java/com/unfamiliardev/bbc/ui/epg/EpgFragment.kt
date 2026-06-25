/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.ui.epg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.repository.EpgRepository
import com.unfamiliardev.bbc.util.AppSettings
import com.unfamiliardev.bbc.ui.browse.BrowseViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EpgFragment : Fragment() {

    private lateinit var viewModel: BrowseViewModel

    private lateinit var epgRows: RecyclerView
    private lateinit var timeHeaderScroll: HorizontalScrollView
    private lateinit var timeSlotsContainer: LinearLayout
    private lateinit var epgDate: TextView

    private var epgAdapter: EpgAdapter? = null
    private var dayStartMs: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_epg, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        epgRows = view.findViewById(R.id.epg_rows)
        timeHeaderScroll = view.findViewById(R.id.time_header_scroll)
        timeSlotsContainer = view.findViewById(R.id.time_slots_container)
        epgDate = view.findViewById(R.id.epg_date)

        viewModel = ViewModelProvider(this)[BrowseViewModel::class.java]
        epgRows.layoutManager = LinearLayoutManager(requireContext())

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dayStartMs = cal.timeInMillis

        epgDate.text = SimpleDateFormat("EEE d MMM", Locale.getDefault()).format(Date())

        viewModel.channels.observe(viewLifecycleOwner) { channels ->
            buildTimeHeader()
            buildEpgRows(channels)

            val epgUrl = AppSettings.getEpgUrl(requireContext())
            if (epgUrl.isNotEmpty()) {
                lifecycleScope.launch {
                    val result = EpgRepository.fetchAndCache(epgUrl)
                    if (result != null) {
                        buildEpgRows(channels)
                    }
                }
            }
        }

        timeHeaderScroll.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            epgAdapter?.syncAllScrollTo(scrollX)
        }
    }

    private fun buildTimeHeader() {
        timeSlotsContainer.removeAllViews()
        val density = resources.displayMetrics.density
        val slotWidthPx = (30 * EpgAdapter.PX_PER_MIN * density).toInt()

        for (slot in 0 until 48) {
            val hour = slot / 2
            val minute = if (slot % 2 == 0) "00" else "30"
            val label = "%02d:%s".format(hour, minute)

            val tv = TextView(requireContext()).apply {
                text = label
                textSize = 12f
                setTextColor(resources.getColor(R.color.secondary_text, null))
                gravity = android.view.Gravity.CENTER
            }

            val params = LinearLayout.LayoutParams(slotWidthPx, LinearLayout.LayoutParams.MATCH_PARENT)
            timeSlotsContainer.addView(tv, params)
        }
    }

    private fun buildEpgRows(channels: List<Channel>) {
        val epgData = EpgRepository.getCached() ?: com.unfamiliardev.bbc.data.parser.XmltvParser.EpgData(emptyMap())

        epgAdapter = EpgAdapter(
            context = requireContext(),
            channels = channels,
            epgData = epgData,
            dayStartMs = dayStartMs,
            onScrollChanged = { scrollX ->
                timeHeaderScroll.scrollTo(scrollX, 0)
                epgAdapter?.syncAllScrollTo(scrollX)
            }
        )

        epgRows.adapter = epgAdapter
    }
}
