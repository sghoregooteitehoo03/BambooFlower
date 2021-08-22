package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.QuestPagingAdapter
import com.sg.android.bambooflower.data.Quest
import com.sg.android.bambooflower.databinding.ItemQuestBinding

class QuestViewHolder(val binding: ItemQuestBinding, itemListener: QuestPagingAdapter.QuestItemListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.onQuestClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Quest?, usersQuestIdList: List<Int>) {
        // 유저가 수행중인 퀘스트이면 알파값을 줌
        val alphaValue = if (usersQuestIdList.contains(data?.id)) {
            0.3f
        } else {
            1f
        }

        binding.quest = data
        binding.alpha = alphaValue
    }
}