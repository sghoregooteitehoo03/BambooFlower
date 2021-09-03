package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.ShopItemViewHolder
import com.sg.android.bambooflower.data.Shop
import com.sg.android.bambooflower.databinding.ItemShopBinding

class ShopAdapter : RecyclerView.Adapter<ShopItemViewHolder>() {
    private var list = listOf<Shop>()
    private lateinit var listener: ShopItemListener

    interface ShopItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShopItemViewHolder {
        val view =
            ItemShopBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShopItemViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ShopItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun setOnShopItemListener(_listener: ShopItemListener) {
        listener = _listener
    }

    fun syncData(_list: List<Shop>) {
        list = _list
        notifyDataSetChanged()
    }

    fun getItem(pos: Int) =
        list[pos]
}