/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.data.parser

import android.util.Xml
import com.unfamiliardev.bbc.data.model.Programme
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

object XmltvParser {

    data class EpgData(val programmes: Map<String, List<Programme>>)

    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US)

    fun parse(inputStream: InputStream): EpgData {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        parser.setInput(inputStream, null)

        val programmes = mutableMapOf<String, MutableList<Programme>>()

        var eventType = parser.eventType
        var currentChannelId: String? = null
        var currentStart: Long = 0
        var currentStop: Long = 0
        var currentTitle: String? = null
        var currentDesc: String? = null
        var inProgramme = false
        var inTitle = false
        var inDesc = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (parser.name) {
                        "programme" -> {
                            inProgramme = true
                            currentChannelId = parser.getAttributeValue(null, "channel")
                            val startStr = parser.getAttributeValue(null, "start")
                            val stopStr = parser.getAttributeValue(null, "stop")
                            currentStart = runCatching { dateFormat.parse(startStr)?.time ?: 0L }.getOrDefault(0L)
                            currentStop = runCatching { dateFormat.parse(stopStr)?.time ?: 0L }.getOrDefault(0L)
                            currentTitle = null
                            currentDesc = null
                        }
                        "title" -> if (inProgramme) inTitle = true
                        "desc" -> if (inProgramme) inDesc = true
                    }
                }
                XmlPullParser.TEXT -> {
                    when {
                        inTitle -> currentTitle = parser.text
                        inDesc -> currentDesc = parser.text
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (parser.name) {
                        "title" -> inTitle = false
                        "desc" -> inDesc = false
                        "programme" -> {
                            val channelId = currentChannelId
                            val title = currentTitle
                            if (inProgramme && channelId != null && title != null) {
                                val programme = Programme(
                                    channelId = channelId,
                                    title = title,
                                    start = currentStart,
                                    stop = currentStop,
                                    description = currentDesc
                                )
                                programmes.getOrPut(channelId) { mutableListOf() }.add(programme)
                            }
                            inProgramme = false
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        return EpgData(programmes)
    }
}
