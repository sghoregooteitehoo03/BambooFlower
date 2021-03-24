package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.DiaryPagingAdapter
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.ItemDiaryBinding

class DiaryViewHolder(
    private val binding: ItemDiaryBinding,
    diaryListener: DiaryPagingAdapter.DiaryItemListener
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            diaryListener.onDiaryItemClickListener(bindingAdapterPosition - 1)
        }
    }

    fun bind(diaryData: Diary?) {
        binding.diaryData = diaryData
    }
}