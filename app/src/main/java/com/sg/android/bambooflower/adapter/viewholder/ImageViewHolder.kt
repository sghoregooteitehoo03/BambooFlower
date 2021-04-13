package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.ImageAdapter
import com.sg.android.bambooflower.databinding.ItemImageBinding

class ImageViewHolder(
    private val binding: ItemImageBinding,
    itemListener: ImageAdapter.ImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.removeImage.setOnClickListener {
            itemListener.onRemoveListener(bindingAdapterPosition)
        }
    }

    fun bind(image: Uri) {
        binding.imageUri = image
    }
}