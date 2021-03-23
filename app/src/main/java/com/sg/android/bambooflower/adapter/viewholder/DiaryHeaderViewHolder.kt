package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.databinding.ItemDiaryHeaderBinding

class DiaryHeaderViewHolder(
    private val binding: ItemDiaryHeaderBinding,
    private val clickListener: DiaryPagingAdapter.DiaryItemListener
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            clickListener.addItemClickListener()
        }
    }
}