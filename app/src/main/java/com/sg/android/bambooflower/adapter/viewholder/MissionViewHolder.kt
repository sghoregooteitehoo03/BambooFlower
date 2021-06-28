package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.MissionAdapter
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.ItemMissionBinding

class MissionViewHolder(
    private val binding: ItemMissionBinding,
    private val itemListener: MissionAdapter.MissionItemListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.setOnClickListener {
            itemListener.itemClickListener(bindingAdapterPosition)
        }
    }

    fun bind(data: Mission, user: User) {
        binding.mission = data
        binding.user = user
    }
}