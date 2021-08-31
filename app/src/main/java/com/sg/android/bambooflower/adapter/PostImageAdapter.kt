package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.PostImageViewHolder
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.databinding.ItemPostImageBinding

class PostImageAdapter : RecyclerView.Adapter<PostImageViewHolder>() {
    private var postList = listOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostImageViewHolder {
        val view = ItemPostImageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PostImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    override fun getItemCount(): Int =
        postList.size

    fun syncData(_postList: List<Post>) {
        postList = _postList
        notifyDataSetChanged()
    }
}