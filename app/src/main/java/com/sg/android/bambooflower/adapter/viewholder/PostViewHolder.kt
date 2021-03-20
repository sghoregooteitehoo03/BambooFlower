package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.PostAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val listener: PostAdapter.PostItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(post: Post?) {
        binding.postData = post
    }
}