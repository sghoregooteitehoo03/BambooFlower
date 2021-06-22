package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Accept
import com.sg.android.bambooflower.databinding.ItemAcceptBinding

class AcceptViewHolder(private val binding: ItemAcceptBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: Accept?) {
        binding.acceptPeople = data
    }
}