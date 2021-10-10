package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.PostNativeViewHolder
import com.sg.android.bambooflower.adapter.viewholder.PostViewHolder
import com.sg.android.bambooflower.data.DataType
import com.sg.android.bambooflower.data.PostItemModel
import com.sg.android.bambooflower.databinding.ItemPostBinding
import com.sg.android.bambooflower.databinding.ItemPostNativeBinding

class PostPagingAdapter() :
    PagingDataAdapter<PostItemModel, RecyclerView.ViewHolder>(diffUtil) {

    interface PostItemListener {
        fun onMoreItemClickListener(pos: Int, view: View)
        fun onImageClickListener(pos: Int)
        fun onFavoriteClickListener(pos: Int)
        fun onShowPeopleClickListener(pos: Int)
    }

    private lateinit var mListener: PostItemListener

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostNativeViewHolder) {
            holder.bind((getItem(position) as PostItemModel.Header).nativeAd)
        } else if (holder is PostViewHolder) {
            holder.bind((getItem(position) as PostItemModel.Item).post)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DataType.HEADER.ordinal -> {
                val view =
                    ItemPostNativeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostNativeViewHolder(view)
            }
            else -> {
                val view =
                    ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(view, mListener)
            }
        }
    }

    // 헤더 아이템 확인
    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type?.ordinal ?: DataType.ITEM.ordinal
    }

    fun setOnPostItemListener(_listener: PostItemListener) {
        mListener = _listener
    }

    fun getPost(pos: Int) =
        (getItem(pos) as PostItemModel.Item).post

    private companion object diffUtil : DiffUtil.ItemCallback<PostItemModel>() {
        override fun areItemsTheSame(oldItem: PostItemModel, newItem: PostItemModel): Boolean {
            return if (oldItem is PostItemModel.Item && newItem is PostItemModel.Item) {
                oldItem.post.id == newItem.post.id
            } else {
                oldItem.type.name == newItem.type.name
            }
        }

        override fun areContentsTheSame(oldItem: PostItemModel, newItem: PostItemModel): Boolean {
            return oldItem == newItem
        }
    }
}