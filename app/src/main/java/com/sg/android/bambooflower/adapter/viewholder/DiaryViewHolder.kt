package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.databinding.ItemDiaryBinding

class DiaryViewHolder(private val binding: ItemDiaryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(diaryData: Diary?) {
        binding.diaryData = diaryData
    }
}