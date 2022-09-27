package com.example.searchmusic.presentation.musiclist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.searchmusic.R
import com.example.searchmusic.databinding.MusicListContentBinding

class MusicListAdapter(
    val navigateToDetails: (MusicUiModel) -> Unit
) : PagingDataAdapter<MusicUiModel, MusicListAdapter.ViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MusicListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(private val binding: MusicListContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MusicUiModel) {
            binding.songName.text = item.musicTitle
            binding.artistName.text = item.artisName
            binding.albumName.text = item.albumName
            Glide.with(binding.imageView).load(item.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.image_placeholder).into(binding.imageView)

            binding.root.setOnClickListener {
                navigateToDetails.invoke(item)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<MusicUiModel> =
            object : DiffUtil.ItemCallback<MusicUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MusicUiModel, newItem: MusicUiModel
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: MusicUiModel, newItem: MusicUiModel
                ): Boolean {
                    return oldItem.trackId == newItem.trackId
                }
            }
    }
}