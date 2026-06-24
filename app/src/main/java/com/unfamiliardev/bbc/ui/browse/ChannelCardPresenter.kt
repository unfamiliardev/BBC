/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.browse

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.model.Channel

class ChannelCardPresenter(
    private val onLongClick: ((Channel) -> Unit)? = null
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val card = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT)
        }
        return ViewHolder(card)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val channel = item as Channel
        val card = viewHolder.view as ImageCardView

        card.titleText = channel.name
        card.contentText = channel.group

        if (!channel.logoUrl.isNullOrEmpty()) {
            Glide.with(card.context)
                .load(channel.logoUrl)
                .placeholder(R.drawable.ic_channel_placeholder)
                .error(R.drawable.ic_channel_placeholder)
                .into(card.mainImageView!!)
        } else {
            card.mainImage = card.context.getDrawable(R.drawable.ic_channel_placeholder)
        }

        onLongClick?.let { callback ->
            card.setOnLongClickListener {
                callback(channel)
                true
            }
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        val card = viewHolder.view as ImageCardView
        Glide.with(card.context).clear(card.mainImageView!!)
        card.mainImage = null
        card.setOnLongClickListener(null)
    }

    companion object {
        private const val CARD_WIDTH = 220
        private const val CARD_HEIGHT = 124
    }
}
