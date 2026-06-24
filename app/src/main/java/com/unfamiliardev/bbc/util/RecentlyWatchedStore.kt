/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.content.Context
import com.unfamiliardev.bbc.data.model.Channel

object RecentlyWatchedStore {

    private const val PREFS = "bbc_recently_watched"
    private const val KEY = "recent"
    private const val MAX = 12
    private const val FIELD_SEP = ""
    private const val ROW_SEP = ""

    fun record(context: Context, channel: Channel) {
        val list = get(context).toMutableList()
        list.removeIf { it.url == channel.url }
        list.add(0, channel)
        if (list.size > MAX) list.subList(MAX, list.size).clear()
        save(context, list)
    }

    fun get(context: Context): List<Channel> {
        val s = prefs(context).getString(KEY, "").orEmpty()
        if (s.isBlank()) return emptyList()
        return s.split(ROW_SEP).mapNotNull { row ->
            val p = row.split(FIELD_SEP)
            if (p.size < 5) null
            else Channel(
                id         = p[1],
                name       = p[0],
                url        = p[1],
                logoUrl    = p[2].ifEmpty { null },
                group      = p[3],
                playlistId = p[4].toLongOrNull() ?: -1L
            )
        }
    }

    private fun save(context: Context, channels: List<Channel>) {
        val s = channels.joinToString(ROW_SEP) { ch ->
            listOf(ch.name, ch.url, ch.logoUrl.orEmpty(), ch.group, ch.playlistId.toString())
                .joinToString(FIELD_SEP)
        }
        prefs(context).edit().putString(KEY, s).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
