package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.databinding.ItemMissionBinding

class MissionViewHolder(private val binding: ItemMissionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(mission: Mission?, uid: String) {
        with(binding) {
            this.mission = mission
            this.uid = uid
        }
    }
}