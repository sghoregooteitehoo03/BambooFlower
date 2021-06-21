package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.AcceptViewHolder
import com.sg.android.bambooflower.data.Accept
import com.sg.android.bambooflower.databinding.ItemAcceptBinding

class AcceptPagingAdapter : PagingDataAdapter<Accept, AcceptViewHolder>(diffUtil) {

    override fun onBindViewHolder(holder: AcceptViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptViewHolder {
        val view = ItemAcceptBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcceptViewHolder(view)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Accept>() {
        override fun areItemsTheSame(oldItem: Accept, newItem: Accept): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Accept, newItem: Accept): Boolean {
            return oldItem == newItem
        }
    }
}