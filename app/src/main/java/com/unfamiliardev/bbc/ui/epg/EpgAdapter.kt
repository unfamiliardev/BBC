/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.ui.epg

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.parser.XmltvParser

class EpgAdapter(
    private val context: Context,
    private val channels: List<Channel>,
    private val epgData: XmltvParser.EpgData,
    private val dayStartMs: Long,
    private val onScrollChanged: (Int) -> Unit
) : RecyclerView.Adapter<EpgAdapter.EpgRowHolder>() {

    companion object {
        const val PX_PER_MIN = 4f
    }

    private val density = context.resources.displayMetrics.density
    private val dayEndMs = dayStartMs + 24 * 60 * 60 * 1000L
    private val scrollViews = mutableListOf<HorizontalScrollView>()
    private var syncScrollX = 0

    inner class EpgRowHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val epgLogo: ImageView = itemView.findViewById(R.id.epg_logo)
        val epgName: TextView = itemView.findViewById(R.id.epg_ch_name)
        val progScroll: HorizontalScrollView = itemView.findViewById(R.id.prog_scroll)
        val progContainer: LinearLayout = itemView.findViewById(R.id.prog_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpgRowHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_epg_row, parent, false)
        return EpgRowHolder(view)
    }

    override fun onBindViewHolder(holder: EpgRowHolder, position: Int) {
        val channel = channels[position]

        Glide.with(context)
            .load(channel.logoUrl)
            .placeholder(R.drawable.ic_channel_placeholder)
            .error(R.drawable.ic_channel_placeholder)
            .fitCenter()
            .into(holder.epgLogo)

        holder.epgName.text = channel.name

        holder.progContainer.removeAllViews()

        val programmes = epgData.programmes[channel.tvgId]
            ?: epgData.programmes[channel.name]
            ?: emptyList()

        val padding8dp = (8 * density).toInt()
        val margin1dp = (1 * density).toInt()

        for (prog in programmes) {
            if (prog.stop <= dayStartMs || prog.start >= dayEndMs) continue

            val leftMargin = ((prog.start - dayStartMs) / 60000f * PX_PER_MIN * density).toInt()
            val width = ((prog.stop - prog.start) / 60000f * PX_PER_MIN * density).toInt().coerceAtLeast(1)

            val textView = TextView(context).apply {
                text = prog.title
                setTextColor(Color.WHITE)
                textSize = 12f
                ellipsize = TextUtils.TruncateAt.END
                maxLines = 1
                gravity = Gravity.CENTER_VERTICAL
                setPadding(padding8dp, padding8dp, padding8dp, padding8dp)
                isFocusable = true
                setBackgroundResource(R.drawable.programme_bg)
            }

            val params = LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(leftMargin + margin1dp, margin1dp, margin1dp, margin1dp)
            }
            holder.progContainer.addView(textView, params)
        }

        if (!scrollViews.contains(holder.progScroll)) {
            scrollViews.add(holder.progScroll)
        }

        holder.progScroll.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            onScrollChanged(scrollX)
        }

        holder.progScroll.post {
            holder.progScroll.scrollTo(syncScrollX, 0)
        }
    }

    override fun getItemCount(): Int = channels.size

    fun syncAllScrollTo(x: Int) {
        syncScrollX = x
        for (sv in scrollViews) {
            sv.scrollTo(x, 0)
        }
    }
}
