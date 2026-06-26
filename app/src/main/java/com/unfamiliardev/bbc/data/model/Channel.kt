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
