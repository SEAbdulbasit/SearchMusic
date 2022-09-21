package com.example.searchmusic.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.searchmusic.ItemDetailFragment
import com.example.searchmusic.R
import com.example.searchmusic.databinding.MusicListContentBinding
import com.example.searchmusic.placeholder.PlaceholderContent
import com.example.searchmusic.presentation.musiclist.MusicUiModel

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
        val item = getItem(position)
        item?.let {
            holder.songName.text = item.songName
            holder.artistName.text = item.artisName
            Glide.with(holder.image).load(item.imageUrl).into(holder.image);

            with(holder.itemView) {
                tag = item
                setOnClickListener { itemView ->
                    val item = itemView.tag as PlaceholderContent.PlaceholderItem
                    val bundle = Bundle()
                    bundle.putString(
                        ItemDetailFragment.ARG_ITEM_ID, item.id
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
    }

    inner class ViewHolder(binding: MusicListContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val songName: TextView = binding.songName
        val artistName: TextView = binding.artistName
        val image: ImageView = binding.imageView
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
                    return oldItem.id == newItem.id
                }
            }
    }
}