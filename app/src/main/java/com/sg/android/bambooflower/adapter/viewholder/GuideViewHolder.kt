package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.GuideAdapter
import com.sg.android.bambooflower.data.Guide
import com.sg.android.bambooflower.databinding.ItemGuideBinding

class GuideViewHolder(
    private val binding: ItemGuideBinding,
    private val itemListener: GuideAdapter.GuideItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.mainLayout.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Guide) {
        binding.guideData = data
    }
}