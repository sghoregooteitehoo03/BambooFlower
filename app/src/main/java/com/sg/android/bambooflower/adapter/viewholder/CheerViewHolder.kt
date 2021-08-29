package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Cheer
import com.sg.android.bambooflower.databinding.ItemCheerBinding

class CheerViewHolder(private val binding: ItemCheerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Cheer?) {
        binding.cheerData = data
    }
}