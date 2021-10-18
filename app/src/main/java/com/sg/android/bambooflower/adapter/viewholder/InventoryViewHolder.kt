package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.InventoryAdapter
import com.sg.android.bambooflower.data.Inventory
import com.sg.android.bambooflower.databinding.ItemInventoryBinding

class InventoryViewHolder(
    private val binding: ItemInventoryBinding,
    private val listener: InventoryAdapter.InventoryItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.itemImage.setOnLongClickListener {
            listener.onItemLongClickListener(it, bindingAdapterPosition)
            true
        }
        binding.itemImage.setOnClickListener {
            listener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Inventory) {
        binding.inventory = data
        binding.itemLayout.alpha = if (data.itemCount != 0 && data.category == Inventory.ITEM_CATEGORY_FLOWER) {
            1f
        } else if (!data.isUsing && data.category == Inventory.ITEM_CATEGORY_WALLPAPER) {
            1f
        } else {
            0.3f
        }
    }
}