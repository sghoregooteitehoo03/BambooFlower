package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.databinding.ItemCollectionFlowerBinding

class CollectionViewHolder(
    private val binding: ItemCollectionFlowerBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(flowerData: Flower) {
        binding.flower = flowerData
        binding.alpha = if(flowerData.isExists) {
            1f
        } else {
            0.3f
        }
    }
}