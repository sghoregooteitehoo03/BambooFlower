package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.MissionImageAdapter
import com.sg.android.bambooflower.databinding.ItemMissionImageOtherBinding

class MissionImageOtherViewHolder(
    private val binding: ItemMissionImageOtherBinding,
    itemListener: MissionImageAdapter.ImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition, true)
        }
    }

    fun bind(image: Uri) {
        binding.imageUri = image
    }
}