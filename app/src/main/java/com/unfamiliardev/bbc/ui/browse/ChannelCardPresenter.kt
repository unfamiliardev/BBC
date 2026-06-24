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
import kotlin.math.abs

class ChannelCardPresenter(
    private val onLongClick: ((Channel) -> Unit)? = null
) : Presenter() {

    inner class CardViewHolder(view: View) : ViewHolder(view) {
        val colorBg: View    = view.findViewById(R.id.card_color_bg)
        val image: ImageView = view.findViewById(R.id.card_image)
        val initial: TextView = view.findViewById(R.id.card_initial)
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
        holder.group.text = channel.group.takeIf { it.isNotEmpty() }

        if (!channel.logoUrl.isNullOrEmpty()) {
            holder.colorBg.visibility = View.GONE
            holder.initial.visibility = View.GONE
            Glide.with(holder.image.context)
                .load(channel.logoUrl)
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .centerCrop()
                .into(holder.image)
        } else {
            holder.image.setImageDrawable(null)
            holder.colorBg.visibility = View.VISIBLE
            holder.colorBg.setBackgroundColor(avatarColor(channel.name))
            holder.initial.visibility = View.VISIBLE
            holder.initial.text = channel.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
        }

        holder.view.setOnFocusChangeListener { v, focused ->
            v.animate()
                .scaleX(if (focused) 1.08f else 1f)
                .scaleY(if (focused) 1.08f else 1f)
                .translationZ(if (focused) 12f else 0f)
                .setDuration(160)
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

    companion object {
        // Vivid palette — one per channel based on name hash for consistency
        private val AVATAR_PALETTE = intArrayOf(
            0xFF4F46E5.toInt(), // indigo
            0xFF7C3AED.toInt(), // violet
            0xFF2563EB.toInt(), // blue
            0xFF0891B2.toInt(), // cyan
            0xFF059669.toInt(), // emerald
            0xFFD97706.toInt(), // amber
            0xFFDC2626.toInt(), // red
            0xFF9333EA.toInt(), // purple
            0xFF0284C7.toInt(), // sky
            0xFF16A34A.toInt(), // green
            0xFFDB2777.toInt(), // pink
            0xFFEA580C.toInt(), // orange
        )

        private fun avatarColor(name: String): Int =
            AVATAR_PALETTE[abs(name.hashCode()) % AVATAR_PALETTE.size]
    }
}
