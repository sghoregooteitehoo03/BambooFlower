package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostBinding

class PostViewHolder(
    private val binding: ItemPostBinding,
    private val listener: PostPagingAdapter.PostItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.moreBtn.setOnClickListener {
            listener.onMoreItemClickListener(bindingAdapterPosition, it)
        }
        binding.postImage.setOnClickListener {
            listener.onImageClickListener(bindingAdapterPosition)
        }
        binding.favoriteLayout.setOnClickListener {
            listener.onFavoriteClickListener(bindingAdapterPosition)
        }
        binding.showPeopleBtn.setOnClickListener {
            listener.onShowPeopleClickListener(bindingAdapterPosition)
        }
    }

    fun bind(post: Post?) {
        with(binding) {
            this.postData = post
        }
    }
}