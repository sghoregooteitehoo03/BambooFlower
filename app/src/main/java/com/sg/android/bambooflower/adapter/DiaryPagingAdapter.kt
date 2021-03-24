package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.DiaryHeaderViewHolder
import com.sg.android.bambooflower.adapter.viewholder.DiaryViewHolder
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.ItemDiaryBinding
import com.sg.android.bambooflower.databinding.ItemDiaryHeaderBinding

class DiaryPagingAdapter : PagingDataAdapter<Diary, RecyclerView.ViewHolder>(diffUtil) {
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private lateinit var mListener: DiaryItemListener

    interface DiaryItemListener {
        fun addItemClickListener()
        fun onDiaryItemClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DiaryViewHolder) {
            holder.bind(getItem(position - 1))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = if (viewType == TYPE_HEADER) {
            val view =
                ItemDiaryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DiaryHeaderViewHolder(view, mListener)
        } else {
            val view = ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DiaryViewHolder(view, mListener)
        }

        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> TYPE_HEADER
            else -> TYPE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    fun setOnDiaryItemListener(_listener: DiaryItemListener) {
        mListener = _listener
    }

    fun getDiaryItem(pos: Int) =
        getItem(pos)

    private companion object diffUtil : DiffUtil.ItemCallback<Diary>() {
        override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
            return oldItem == newItem
        }
    }
}