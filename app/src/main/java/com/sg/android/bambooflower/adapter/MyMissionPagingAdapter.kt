package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.MyMissionViewHolder
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.databinding.ItemMyMissionBinding

class MyMissionPagingAdapter(private val uid: String) : PagingDataAdapter<Mission, MyMissionViewHolder>(diffUtil) {
    override fun onBindViewHolder(holder: MyMissionViewHolder, position: Int) {
        holder.bind(getItem(position), uid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMissionViewHolder {
        val view = ItemMyMissionBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MyMissionViewHolder(view)
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