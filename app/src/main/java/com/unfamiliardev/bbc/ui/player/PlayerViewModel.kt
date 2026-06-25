/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val _channelName = MutableLiveData("")
    val channelName: LiveData<String> = _channelName

    private val _channelUrl = MutableLiveData("")
    val channelUrl: LiveData<String> = _channelUrl

    fun setChannel(name: String, url: String) {
        _channelName.value = name
        _channelUrl.value = url
    }
}
