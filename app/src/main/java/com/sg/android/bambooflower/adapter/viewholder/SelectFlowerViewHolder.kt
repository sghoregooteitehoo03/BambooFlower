package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.SelectFlowerAdapter
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.databinding.ItemSelectFlowerBinding

class SelectFlowerViewHolder(
    private val binding: ItemSelectFlowerBinding,
    private val itemListener: SelectFlowerAdapter.FlowerItemListener
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(flowerData: Flower) {
        binding.flower = flowerData
    }
}