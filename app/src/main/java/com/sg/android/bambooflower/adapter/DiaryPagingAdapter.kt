package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.DiaryHeaderViewHolder
import com.sg.android.bambooflower.adapter.viewholder.DiaryViewHolder
import com.sg.android.bambooflower.data.DataType
import com.sg.android.bambooflower.data.DiaryItemModel
import com.sg.android.bambooflower.databinding.ItemDiaryBinding
import com.sg.android.bambooflower.databinding.ItemDiaryHeaderBinding

class DiaryPagingAdapter : PagingDataAdapter<DiaryItemModel, RecyclerView.ViewHolder>(diffUtil) {
    private lateinit var mListener: DiaryItemListener

    interface DiaryItemListener {
        fun onDiaryItemClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DiaryHeaderViewHolder) { // 헤더
            holder.bind((getItem(position) as DiaryItemModel.Header).nativeAd)
        } else if (holder is DiaryViewHolder) { // 아이템
            holder.bind((getItem(position) as DiaryItemModel.Item).diary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DataType.HEADER.ordinal -> { // 헤더
                val view =
                    ItemDiaryHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DiaryHeaderViewHolder(view)
            }
            else -> { // 아이템
                val view =
                    ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DiaryViewHolder(view, mListener)
            }
        }
    }

    // 헤더 아이템 확인
    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type?.ordinal ?: DataType.ITEM.ordinal
    }

    fun setOnDiaryItemListener(_listener: DiaryItemListener) {
        mListener = _listener
    }

    fun getDiaryItem(pos: Int) =
        (getItem(pos) as DiaryItemModel.Item).diary

    private companion object diffUtil : DiffUtil.ItemCallback<DiaryItemModel>() {
        override fun areItemsTheSame(oldItem: DiaryItemModel, newItem: DiaryItemModel): Boolean {
            return if (oldItem is DiaryItemModel.Item && newItem is DiaryItemModel.Item) {
                oldItem.diary.id == newItem.diary.id
            } else {
                oldItem.type.name == newItem.type.name
            }
        }

        override fun areContentsTheSame(oldItem: DiaryItemModel, newItem: DiaryItemModel): Boolean {
            return oldItem == newItem
        }
    }
}