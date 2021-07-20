package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemMyPhotoBinding

class MyPhotoViewHolder(private val binding: ItemMyPhotoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post?) {
        binding.postData = post
    }
}