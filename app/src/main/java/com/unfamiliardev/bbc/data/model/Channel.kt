/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.data.model

data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val logoUrl: String?,
    val group: String,
    val playlistId: Long,
    val tvgId: String? = null
)
