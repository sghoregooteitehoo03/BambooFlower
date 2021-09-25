package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.CollocateAdapter
import com.sg.android.bambooflower.databinding.ItemCollocateBinding

class CollocateViewHolder(
    private val binding: ItemCollocateBinding,
    private val listener: CollocateAdapter.CollocateItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.mainLayout.setOnDragListener { v, event ->
            listener.onItemDragListener(v, event, bindingAdapterPosition)
            true
        }
    }

    fun bind(data: Boolean, isCollocated: Boolean) {
        binding.isShowing = data
        binding.isCollocated = isCollocated
    }
}