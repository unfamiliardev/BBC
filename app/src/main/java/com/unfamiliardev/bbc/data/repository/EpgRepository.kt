/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.data.repository

import com.unfamiliardev.bbc.data.model.Programme
import com.unfamiliardev.bbc.data.parser.XmltvParser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object EpgRepository {

    private var cachedData: XmltvParser.EpgData? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun fetchAndCache(url: String): XmltvParser.EpgData? = withContext(IO) {
        runCatching {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body ?: return@runCatching null
            val result = XmltvParser.parse(body.byteStream())
            cachedData = result
            result
        }.getOrNull()
    }

    fun getCached(): XmltvParser.EpgData? = cachedData

    fun getNowPlaying(channelId: String): Programme? {
        val now = System.currentTimeMillis()
        return cachedData?.programmes?.get(channelId)?.firstOrNull { prog ->
            now >= prog.start && now < prog.stop
        }
    }

    fun clearCache() {
        cachedData = null
    }
}
