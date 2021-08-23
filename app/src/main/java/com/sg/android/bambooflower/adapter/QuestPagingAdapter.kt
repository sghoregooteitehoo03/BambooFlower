package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.sg.android.bambooflower.adapter.viewholder.QuestViewHolder
import com.sg.android.bambooflower.data.Quest
import com.sg.android.bambooflower.databinding.ItemQuestBinding

class QuestPagingAdapter() : PagingDataAdapter<Quest, QuestViewHolder>(diffUtil) {
    private var usersQuestIdList = listOf<Int>()
    private lateinit var mListener: QuestItemListener

    interface QuestItemListener {
        fun onQuestClickListener(pos: Int)
    }

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        holder.bind(getItem(position), usersQuestIdList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = ItemQuestBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestViewHolder(view, mListener)
    }

    private companion object diffUtil : DiffUtil.ItemCallback<Quest>() {
        override fun areItemsTheSame(oldItem: Quest, newItem: Quest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Quest, newItem: Quest): Boolean {
            return oldItem == newItem
        }
    }

    fun getItemData(pos: Int) =
        getItem(pos)!!

    // 유저가 퀘스트를 수락하였는지 확인
    fun isQuestPerform(id: Int) =
        usersQuestIdList.contains(id)


    fun syncData(_usersQuestIdList: List<Int>) {
        usersQuestIdList = _usersQuestIdList
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: QuestItemListener) {
        mListener = _listener
    }
}