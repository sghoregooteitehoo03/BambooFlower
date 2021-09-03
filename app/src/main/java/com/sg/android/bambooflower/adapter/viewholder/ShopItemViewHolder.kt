package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.ShopAdapter
import com.sg.android.bambooflower.data.Shop
import com.sg.android.bambooflower.databinding.ItemShopBinding

class ShopItemViewHolder(
    private val binding: ItemShopBinding,
    private val listener: ShopAdapter.ShopItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            listener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Shop) {
        binding.shopData = data
    }
}