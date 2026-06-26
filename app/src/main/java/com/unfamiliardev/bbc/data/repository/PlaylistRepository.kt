/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.data.repository

import android.content.Context
import com.unfamiliardev.bbc.data.db.AppDatabase
import com.unfamiliardev.bbc.data.db.PlaylistEntity
import com.unfamiliardev.bbc.data.model.Channel
import com.unfamiliardev.bbc.data.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class PlaylistRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).playlistDao()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val playlists: Flow<List<PlaylistEntity>> = dao.getAll()

    suspend fun addPlaylist(name: String, url: String): Long =
        dao.insert(PlaylistEntity(name = name, url = url))

    suspend fun deletePlaylist(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.deleteAll()

    suspend fun fetchChannels(): List<Channel> = withContext(Dispatchers.IO) {
        val all = mutableListOf<Channel>()
        for (entity in dao.getAllOnce()) {
            try {
                all.addAll(M3UParser.parse(fetchUrl(entity.url), entity.id))
            } catch (_: IOException) {
                // skip unreachable playlist
            }
        }
        all
    }

    private fun fetchUrl(url: String): String {
        val request = Request.Builder().url(url).build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("HTTP ${response.code}")
            response.body?.string() ?: throw IOException("Empty body")
        }
    }
}
