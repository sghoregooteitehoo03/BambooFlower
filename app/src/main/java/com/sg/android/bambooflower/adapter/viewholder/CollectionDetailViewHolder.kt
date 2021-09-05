package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Inventory
import com.sg.android.bambooflower.databinding.ItemCollectionDetailBinding

class CollectionDetailViewHolder(
    private val binding: ItemCollectionDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Inventory) {
        binding.inventory = data
        binding.alpha = if (data.id != null) {
            1f
        } else {
            0.3f
        }
    }
}