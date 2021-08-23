package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.databinding.PagerImageBinding

class PagerImageViewHolder(val binding: PagerImageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(image: String) {
        binding.image = image
    }
}