package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.DiaryViewHolder
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.ItemDiaryBinding

class DiaryPagingAdapter : PagingDataAdapter<Diary, DiaryViewHolder>(diffUtil) {

    private lateinit var mListener: DiaryItemListener

    interface DiaryItemListener {
        fun onDiaryItemClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view =
            ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiaryViewHolder(view, mListener)
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