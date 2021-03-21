package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.PostViewHolder
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostBinding

class PostAdapter : RecyclerView.Adapter<PostViewHolder>() {
    private var posts = listOf<Post>()
    private lateinit var mListener: PostPagingAdapter.PostItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun setOnPostListener(_listener: PostPagingAdapter.PostItemListener) {
        mListener = _listener
    }

    fun syncData(_posts: List<Post>) {
        posts = _posts
        notifyDataSetChanged()
    }

    fun getItem(pos: Int) =
        posts[pos]
}