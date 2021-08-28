package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.PostViewHolder
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostBinding

class PostPagingAdapter() :
    PagingDataAdapter<Post, PostViewHolder>(diffUtil) {

    interface PostItemListener {
        fun onMoreItemClickListener(pos: Int, view: View)
        fun onImageClickListener(pos: Int)
        fun onFavoriteClickListener(pos: Int)
        fun onShowPeopleClickListener(pos: Int)
    }

    private lateinit var mListener: PostItemListener

    override fun onBindViewHolder(holderPreview: PostViewHolder, position: Int) {
        holderPreview.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, mListener)
    }

    fun setOnPostItemListener(_listener: PostItemListener) {
        mListener = _listener
    }

    fun getPost(pos: Int) =
        getItem(pos)!!

    private companion object diffUtil : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}