/*
 * BBC IPTV App
 * Copyright (c) 2026 UnfamiliarDev and contributors
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package com.unfamiliardev.bbc.data.model

data class Programme(
    val channelId: String,
    val title: String,
    val start: Long,
    val stop: Long,
    val description: String? = null
)
