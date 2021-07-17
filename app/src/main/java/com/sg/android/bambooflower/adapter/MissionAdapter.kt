package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.MissionViewHolder
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.ItemMissionBinding

class MissionAdapter() : RecyclerView.Adapter<MissionViewHolder>() {
    private var missionList = listOf<Mission>()

    private lateinit var user: User
    private lateinit var itemListener: MissionItemListener

    interface MissionItemListener {
        fun itemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionViewHolder {
        val view = ItemMissionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MissionViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: MissionViewHolder, position: Int) {
        holder.bind(missionList[position], user)
    }

    override fun getItemCount(): Int {
        return missionList.size
    }

    fun getItem(pos: Int) =
        missionList[pos]

    fun submitData(_missionList: List<Mission>, _user: User) {
        missionList = _missionList.filterNot {
            it.document == _user.missionDoc
        }
        user = _user

        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: MissionItemListener) {
        itemListener = _listener
    }
}