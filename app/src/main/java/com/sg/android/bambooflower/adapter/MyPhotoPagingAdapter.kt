package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.MyPhotoViewHolder
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemMyPhotoBinding

class MyPhotoPagingAdapter() : PagingDataAdapter<Post, MyPhotoViewHolder>(diffUtil) {
    override fun onBindViewHolder(holder: MyPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPhotoViewHolder {
        val view = ItemMyPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPhotoViewHolder(view)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.postPath == newItem.postPath
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}