package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.UsersQuestViewHolder
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.databinding.ItemMyQuestBinding

class UsersQuestAdapter() : RecyclerView.Adapter<UsersQuestViewHolder>() {
    private val questList = mutableListOf<UsersQuest?>(null, null)
    private lateinit var itemListener: UsersQuestItemListener

    interface UsersQuestItemListener {
        fun usersQuestClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersQuestViewHolder {
        val view = ItemMyQuestBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UsersQuestViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: UsersQuestViewHolder, position: Int) {
        holder.bind(questList[position])
    }

    override fun getItemCount(): Int {
        return questList.size
    }

    fun getItem(pos: Int) =
        questList[pos]

    fun syncData(_questList: List<UsersQuest>) {
        questList.fill(null)
        for (i in _questList.indices) {
            questList[i] = _questList[i]
        }

        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: UsersQuestItemListener) {
        itemListener = _listener
    }
}