/*
 * BBC â€” Open-source Android TV IPTV client
 * Copyright (c) 2026 unfamiliardev
 * SPDX-License-Identifier: Apache-2.0
 */

package com.unfamiliardev.bbc.ui.playlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.unfamiliardev.bbc.util.LocaleHelper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unfamiliardev.bbc.R
import com.unfamiliardev.bbc.data.db.PlaylistEntity
import com.unfamiliardev.bbc.databinding.ActivityPlaylistBinding
import com.unfamiliardev.bbc.ui.credits.CreditsActivity
import com.unfamiliardev.bbc.util.KonamiCodeDetector

class PlaylistActivity : FragmentActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private val viewModel: PlaylistViewModel by viewModels()
    private lateinit var adapter: PlaylistAdapter

    private val konamiDetector = KonamiCodeDetector {
        startActivity(Intent(this, CreditsActivity::class.java))
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PlaylistAdapter(onDelete = { entity -> viewModel.deletePlaylist(entity.id) })
        binding.playlistRecycler.layoutManager = LinearLayoutManager(this)
        binding.playlistRecycler.adapter = adapter

        viewModel.playlists.observe(this) { list ->
            adapter.submitList(list)
            binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.addButton.setOnClickListener { submitPlaylist() }
        binding.inputUrl.setOnEditorActionListener { _, _, _ -> submitPlaylist(); true }
    }

    private fun submitPlaylist() {
        val name = binding.inputName.text.toString().trim()
        val url = binding.inputUrl.text.toString().trim()
        if (url.isNotEmpty()) {
            viewModel.addPlaylist(name.ifEmpty { "Playlist" }, url)
            binding.inputName.text.clear()
            binding.inputUrl.text.clear()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (konamiDetector.onKeyDown(keyCode)) return true
        return super.onKeyDown(keyCode, event)
    }
}

class PlaylistAdapter(
    private val onDelete: (PlaylistEntity) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.VH>() {

    private val items = mutableListOf<PlaylistEntity>()

    fun submitList(list: List<PlaylistEntity>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.playlist_name)
        private val url: TextView = view.findViewById(R.id.playlist_url)
        private val delete: View = view.findViewById(R.id.playlist_delete)

        fun bind(entity: PlaylistEntity) {
            name.text = entity.name
            url.text = entity.url
            delete.isFocusable = true
            delete.setOnClickListener { onDelete(entity) }
            itemView.isFocusable = true
        }
    }
}
