package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostImageBinding

class PostImageViewHolder(
    private val binding: ItemPostImageBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(postData: Post) {
        binding.post = postData
    }
}