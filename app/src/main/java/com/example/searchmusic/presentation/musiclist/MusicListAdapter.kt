package com.example.searchmusic.presentation.musiclist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchmusic.R
import com.example.searchmusic.databinding.MusicListContentBinding
import com.example.searchmusic.presentation.musicdetail.MusicDetailFragment

class MusicListAdapter(
    private val itemDetailFragmentContainer: View?
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
            Glide.with(binding.imageView).load(item.imageUrl).into(binding.imageView)

            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putLong(
                    MusicDetailFragment.ARG_ITEM_ID, item.trackId
                )
                if (itemDetailFragmentContainer != null) {
                    itemDetailFragmentContainer.findNavController()
                        .navigate(R.id.fragment_item_detail, bundle)
                } else {
                    itemView.findNavController().navigate(R.id.show_item_detail, bundle)
                }
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