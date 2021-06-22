package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.DiaryHeaderViewHolder
import com.sg.android.bambooflower.adapter.viewholder.DiaryViewHolder
import com.sg.android.bambooflower.data.DataType
import com.sg.android.bambooflower.data.DiaryDataModel
import com.sg.android.bambooflower.databinding.ItemDiaryBinding
import com.sg.android.bambooflower.databinding.ItemDiaryHeaderBinding

class DiaryPagingAdapter : PagingDataAdapter<DiaryDataModel, RecyclerView.ViewHolder>(diffUtil) {

    private lateinit var mListener: DiaryItemListener

    interface DiaryItemListener {
        fun addItemClickListener()
        fun onDiaryItemClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DiaryViewHolder) {
            holder.bind((getItem(position) as DiaryDataModel.Item).diary)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            DataType.HEADER.ordinal -> {
                val view = ItemDiaryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                DiaryHeaderViewHolder(view, mListener)
            }
            else -> {
                val view =
                    ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                DiaryViewHolder(view, mListener)
            }
        }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type?.ordinal ?: DataType.ITEM.ordinal
    }

    fun setOnDiaryItemListener(_listener: DiaryItemListener) {
        mListener = _listener
    }

    fun getDiaryItem(pos: Int) =
        (getItem(pos + 1) as DiaryDataModel.Item).diary

    private companion object diffUtil : DiffUtil.ItemCallback<DiaryDataModel>() {
        override fun areItemsTheSame(oldItem: DiaryDataModel, newItem: DiaryDataModel): Boolean {
            return if (oldItem is DiaryDataModel.Item && newItem is DiaryDataModel.Item) {
                oldItem.diary.id == newItem.diary.id
            } else {
                oldItem.type.name == newItem.type.name
            }
        }

        override fun areContentsTheSame(oldItem: DiaryDataModel, newItem: DiaryDataModel): Boolean {
            return oldItem == newItem
        }
    }
}