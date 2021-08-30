package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.QuestImageViewHolder
import com.sg.android.bambooflower.databinding.ItemQuestImageBinding

class QuestImageAdapter(private var imageList: List<String>) : RecyclerView.Adapter<QuestImageViewHolder>() {
    private lateinit var mListener: ImageItemListener

    interface ImageItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestImageViewHolder {
        val view = ItemQuestImageBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestImageViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: QuestImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.count()
    }

    fun getItem(pos: Int) =
        imageList[pos]

    fun getList() =
        imageList

    fun setOnImageItemListener(_listener: ImageItemListener) {
        mListener = _listener
    }

    fun syncData(_imageList: List<String>) {
        imageList = _imageList
        notifyDataSetChanged()
    }
}