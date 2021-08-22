package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.UsersQuestAdapter
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.databinding.ItemMyQuestBinding

class UsersQuestViewHolder(
    private val binding: ItemMyQuestBinding,
    private val itemListener: UsersQuestAdapter.UsersQuestItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.usersQuestClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: UsersQuest?) {
       binding.usersQuest = data
    }
}