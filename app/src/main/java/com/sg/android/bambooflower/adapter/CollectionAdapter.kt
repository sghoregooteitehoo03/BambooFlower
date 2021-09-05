package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.CollectionDetailViewHolder
import com.sg.android.bambooflower.adapter.viewholder.CollectionViewHolder
import com.sg.android.bambooflower.data.Inventory
import com.sg.android.bambooflower.databinding.ItemCollectionDetailBinding
import com.sg.android.bambooflower.databinding.ItemCollectionFlowerBinding

class CollectionAdapter(
    private val isDetail: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list = listOf<Inventory>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isDetail) {
            val view = ItemCollectionDetailBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            CollectionDetailViewHolder(view)
        } else {
            val view = ItemCollectionFlowerBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            CollectionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CollectionViewHolder) {
            holder.bind(list[position])
        } else if (holder is CollectionDetailViewHolder) {
            holder.bind(list[position])
        }
    }

    override fun getItemCount(): Int =
        list.size

    fun syncData(_list: List<Inventory>) {
        list = _list
        notifyDataSetChanged()
    }
}