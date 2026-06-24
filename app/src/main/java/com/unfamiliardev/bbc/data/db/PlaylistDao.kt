/*
 * BBC — Open-source Android TV IPTV client
 * Copyright (c) 2024 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists ORDER BY addedAt DESC")
    fun getAll(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists ORDER BY addedAt DESC")
    suspend fun getAllOnce(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deleteById(id: Long)
}
