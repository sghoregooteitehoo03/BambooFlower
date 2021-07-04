package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.databinding.ItemMyMissionBinding

class MyMissionViewHolder(private val binding: ItemMyMissionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(mission: Mission?, uid: String) {
        with(binding) {
            this.mission = mission
            this.uid = uid
        }
    }
}