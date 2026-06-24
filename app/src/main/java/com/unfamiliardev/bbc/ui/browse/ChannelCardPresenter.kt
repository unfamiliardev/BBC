/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.browse

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel

class ChannelCardPresenter(
    private val onLongClick: ((Channel) -> Unit)? = null
) : Presenter() {

    inner class CardViewHolder(view: View) : ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.card_image)
        val title: TextView  = view.findViewById(R.id.card_title)
        val group: TextView  = view.findViewById(R.id.card_group)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel_card, parent, false)
        view.isFocusable = true
        view.isFocusableInTouchMode = true
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val holder = viewHolder as CardViewHolder
        val channel = item as Channel

        holder.title.text = channel.name
        holder.group.text  = channel.group.ifEmpty { null }

        Glide.with(holder.image.context)
            .load(channel.logoUrl)
            .placeholder(R.drawable.ic_channel_placeholder)
            .error(R.drawable.ic_channel_placeholder)
            .centerCrop()
            .into(holder.image)

        holder.view.setOnFocusChangeListener { v, focused ->
            v.animate()
                .scaleX(if (focused) 1.07f else 1f)
                .scaleY(if (focused) 1.07f else 1f)
                .setDuration(140)
                .start()
        }

        onLongClick?.let { cb ->
            holder.view.setOnLongClickListener { cb(channel); true }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val holder = viewHolder as CardViewHolder
        Glide.with(holder.image.context).clear(holder.image)
        holder.view.onFocusChangeListener = null
        holder.view.setOnLongClickListener(null)
    }
}
