/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.data.parser

import com.unfamiliardev.bbc.data.model.Channel
import java.util.UUID

object M3UParser {

    private val EXTINF_REGEX = Regex("""#EXTINF:-?\d+([^,]*),(.+)""")
    private val ATTR_REGEX = Regex("""(\S+?)="([^"]*?)"""")

    fun parse(content: String, playlistId: Long): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF:")) {
                val match = EXTINF_REGEX.find(line)
                if (match != null) {
                    val attrs = parseAttrs(match.groupValues[1])
                    val title = match.groupValues[2].trim()

                    var urlLine = ""
                    var j = i + 1
                    while (j < lines.size) {
                        val candidate = lines[j].trim()
                        if (candidate.isNotEmpty() && !candidate.startsWith("#")) {
                            urlLine = candidate
                            i = j
                            break
                        }
                        j++
                    }

                    if (urlLine.isNotEmpty()) {
                        channels.add(
                            Channel(
                                id = UUID.randomUUID().toString(),
                                name = attrs["tvg-name"]?.ifEmpty { title } ?: title,
                                url = urlLine,
                                logoUrl = attrs["tvg-logo"]?.ifEmpty { null },
                                group = attrs["group-title"]?.ifEmpty { "Uncategorised" } ?: "Uncategorised",
                                playlistId = playlistId,
                                tvgId = attrs["tvg-id"]?.ifEmpty { null }
                            )
                        )
                    }
                }
            }
            i++
        }
        return channels
    }

    private fun parseAttrs(attrString: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        ATTR_REGEX.findAll(attrString).forEach { m ->
            result[m.groupValues[1]] = m.groupValues[2]
        }
        return result
    }
}
