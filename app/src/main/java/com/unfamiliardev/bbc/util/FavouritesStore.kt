/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.util

import android.content.Context
import com.unfamiliardev.bbc.data.model.Channel

object FavouritesStore {

    private const val PREFS = "bbc_favourites"
    private const val KEY_FAVS = "favourites"
    private const val KEY_MY_LIST = "my_list"
    // ASCII Unit Separator / Record Separator — safe in URLs and channel names
    private const val FIELD_SEP = ""
    private const val ROW_SEP = ""

    // ── Favourites ────────────────────────────────────────────────────────────

    fun getFavourites(context: Context): List<Channel> = load(context, KEY_FAVS)

    fun isFavourite(context: Context, url: String): Boolean =
        getFavourites(context).any { it.url == url }

    fun toggleFavourite(context: Context, channel: Channel) {
        val list = getFavourites(context).toMutableList()
        if (list.any { it.url == channel.url }) list.removeIf { it.url == channel.url }
        else list.add(0, channel)
        save(context, KEY_FAVS, list)
    }

    // ── My List ───────────────────────────────────────────────────────────────

    fun getMyList(context: Context): List<Channel> = load(context, KEY_MY_LIST)

    fun isInMyList(context: Context, url: String): Boolean =
        getMyList(context).any { it.url == url }

    fun toggleMyList(context: Context, channel: Channel) {
        val list = getMyList(context).toMutableList()
        if (list.any { it.url == channel.url }) list.removeIf { it.url == channel.url }
        else list.add(0, channel)
        save(context, KEY_MY_LIST, list)
    }

    // ── Serialisation ─────────────────────────────────────────────────────────

    private fun save(context: Context, key: String, channels: List<Channel>) {
        val s = channels.joinToString(ROW_SEP) { ch ->
            listOf(ch.name, ch.url, ch.logoUrl.orEmpty(), ch.group, ch.playlistId.toString())
                .joinToString(FIELD_SEP)
        }
        prefs(context).edit().putString(key, s).apply()
    }

    private fun load(context: Context, key: String): List<Channel> {
        val s = prefs(context).getString(key, "").orEmpty()
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

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
