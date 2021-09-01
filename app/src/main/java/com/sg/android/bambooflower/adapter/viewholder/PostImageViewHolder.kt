package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.PostImageAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostImageBinding

class PostImageViewHolder(
    private val binding: ItemPostImageBinding,
    private val listener: PostImageAdapter.PostImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener.onImageClickListener(bindingAdapterPosition)
        }
    }

    fun bind(postData: Post) {
        binding.post = postData
    }
}