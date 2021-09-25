package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.InventoryViewHolder
import com.sg.android.bambooflower.data.Inventory
import com.sg.android.bambooflower.databinding.ItemInventoryBinding

class InventoryAdapter : RecyclerView.Adapter<InventoryViewHolder>() {
    private var list = listOf<Inventory>()
    private lateinit var listener: InventoryItemListener

    interface InventoryItemListener {
        fun onItemLongClickListener(view: View, pos: Int)
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view =
            ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InventoryViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size

    fun syncData(_list: List<Inventory>) {
        list = _list
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: InventoryItemListener) {
        listener = _listener
    }

    fun getItem(pos: Int) =
        list[pos]
}