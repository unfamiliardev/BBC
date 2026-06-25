/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.ui.channels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.repository.EpgRepository

class ChannelListAdapter(
    private val onFocused: (Channel) -> Unit,
    private val onClicked: (Channel) -> Unit,
    private val onLongClicked: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelListAdapter.ViewHolder>() {

    private var channels: List<Channel> = emptyList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chNumber: TextView = itemView.findViewById(R.id.ch_number)
        val chLogo: ImageView = itemView.findViewById(R.id.ch_logo)
        val chName: TextView = itemView.findViewById(R.id.ch_name)
        val chNow: TextView = itemView.findViewById(R.id.ch_now)
        val chBadge: TextView = itemView.findViewById(R.id.ch_badge)
    }

    fun updateChannels(list: List<Channel>) {
        channels = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]

        holder.chNumber.text = (position + 1).toString().padStart(3, '0')

        Glide.with(holder.chLogo.context)
            .load(channel.logoUrl)
            .placeholder(R.drawable.ic_channel_placeholder)
            .error(R.drawable.ic_channel_placeholder)
            .fitCenter()
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.chLogo)

        holder.chName.text = channel.name

        val nowPlaying = EpgRepository.getNowPlaying(channel.tvgId ?: channel.name)
        if (nowPlaying != null) {
            holder.chNow.text = nowPlaying.title
            holder.chNow.visibility = View.VISIBLE
        } else {
            holder.chNow.visibility = View.GONE
        }

        holder.chBadge.visibility = View.GONE

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                onFocused(channel)
                view.animate().scaleX(1.02f).scaleY(1.02f).setDuration(100).start()
            } else {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
            }
        }

        holder.itemView.setOnClickListener {
            onClicked(channel)
        }

        holder.itemView.setOnLongClickListener {
            onLongClicked(channel)
            true
        }
    }

    override fun getItemCount(): Int = channels.size
}
