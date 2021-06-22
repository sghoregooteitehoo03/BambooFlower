package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.AlbumAdapter
import com.sg.android.bambooflower.data.Album
import com.sg.android.bambooflower.databinding.ItemAlbumBinding

class AlbumViewHolder(val binding: ItemAlbumBinding, val listener: AlbumAdapter.AlbumItemListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(album: Album) {
        with(binding) {
            this.album = album
        }
    }
}