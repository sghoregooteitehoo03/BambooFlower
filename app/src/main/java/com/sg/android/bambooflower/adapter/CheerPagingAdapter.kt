package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.CheerViewHolder
import com.sg.android.bambooflower.data.Cheer
import com.sg.android.bambooflower.databinding.ItemCheerBinding

class CheerPagingAdapter : PagingDataAdapter<Cheer, CheerViewHolder>(diffUtil) {

    override fun onBindViewHolder(holder: CheerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheerViewHolder {
        val view =
            ItemCheerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheerViewHolder(view)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Cheer>() {
        override fun areItemsTheSame(oldItem: Cheer, newItem: Cheer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cheer, newItem: Cheer): Boolean {
            return oldItem == newItem
        }
    }
}