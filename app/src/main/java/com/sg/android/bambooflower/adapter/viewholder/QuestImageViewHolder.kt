package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.QuestImageAdapter
import com.sg.android.bambooflower.databinding.ItemMissionImageBinding

class QuestImageViewHolder(
    private val binding: ItemMissionImageBinding,
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