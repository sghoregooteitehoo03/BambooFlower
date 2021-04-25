package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.MissionViewHolder
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.databinding.ItemMissionBinding

class MissionPagingAdapter(private val uid: String) : PagingDataAdapter<Mission, MissionViewHolder>(diffUtil) {
    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(getItem(position), uid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = ItemMissionBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MissionViewHolder(view)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Mission>() {
        override fun areItemsTheSame(oldItem: Mission, newItem: Mission): Boolean {
            return oldItem.document == newItem.document
        }

        override fun areContentsTheSame(oldItem: Mission, newItem: Mission): Boolean {
            return oldItem == newItem
        }
    }
}