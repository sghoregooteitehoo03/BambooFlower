package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.databinding.PagerImageBinding

class PagerImageViewHolder(val binding: PagerImageBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUri: Uri, hasOptions: Boolean) {
        binding.imageUri = imageUri
        binding.hasOptions = hasOptions
    }
}