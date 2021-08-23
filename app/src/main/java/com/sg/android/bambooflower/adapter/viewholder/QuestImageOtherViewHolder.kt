package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.QuestImageAdapter
import com.sg.android.bambooflower.databinding.ItemMissionImageOtherBinding

class QuestImageOtherViewHolder(
    private val binding: ItemMissionImageOtherBinding,
    itemListener: QuestImageAdapter.ImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(image: Uri) {
        binding.imageUri = image
    }
}