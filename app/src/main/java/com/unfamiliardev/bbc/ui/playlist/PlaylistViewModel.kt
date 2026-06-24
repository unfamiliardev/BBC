/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.playlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.unfamiliardev.bbc.data.repository.PlaylistRepository
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PlaylistRepository(application)
    val playlists = repository.playlists.asLiveData()

    fun addPlaylist(name: String, url: String) {
        viewModelScope.launch { repository.addPlaylist(name, url) }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch { repository.deletePlaylist(id) }
    }
}
