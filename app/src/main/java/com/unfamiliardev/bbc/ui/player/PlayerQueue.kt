package com.unfamiliardev.bbc.ui.player

import com.unfamiliardev.bbc.data.model.Channel

object PlayerQueue {
    var channels: List<Channel> = emptyList()
    var index: Int = -1

    fun prev(): Channel? = if (index > 0) channels[--index] else null
    fun next(): Channel? = if (index < channels.size - 1) channels[++index] else null
}
