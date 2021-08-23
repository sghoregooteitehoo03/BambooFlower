package com.sg.android.bambooflower.adapter.viewholder

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.QuestImageAdapter
import com.sg.android.bambooflower.databinding.ItemQuestBinding
import com.sg.android.bambooflower.databinding.ItemQuestImageBinding

class QuestImageViewHolder(
    private val binding: ItemQuestImageBinding,
    itemListener: QuestImageAdapter.ImageItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onItemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(image: String) {
        binding.image = image
    }
}