package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.PostPreviewViewHolder
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostPreviewBinding

class PostAdapter : RecyclerView.Adapter<PostPreviewViewHolder>() {
    private var posts = listOf<Post>()
    private lateinit var mListener: PostPagingAdapter.PostItemListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostPreviewViewHolder {
        val view = ItemPostPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostPreviewViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holderPreview: PostPreviewViewHolder, position: Int) {
        holderPreview.bind(posts[position])
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