package com.example.searchmusic.presentation.musiclist

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class MusicLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<MusicLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MusicLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MusicLoadStateViewHolder {
        return MusicLoadStateViewHolder.create(parent, retry)
    }
}
