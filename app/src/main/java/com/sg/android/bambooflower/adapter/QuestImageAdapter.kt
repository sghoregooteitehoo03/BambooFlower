package com.sg.android.bambooflower.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.QuestImageOtherViewHolder
import com.sg.android.bambooflower.adapter.viewholder.QuestImageViewHolder
import com.sg.android.bambooflower.databinding.ItemMissionImageBinding
import com.sg.android.bambooflower.databinding.ItemMissionImageOtherBinding

class QuestImageAdapter(private val other: Boolean = false) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var imageList = listOf<Uri>()
    private lateinit var mListener: ImageItemListener

    interface ImageItemListener {
        fun onItemClickListener(pos: Int, isOther: Boolean = false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (other) {
            val view = ItemMissionImageOtherBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            QuestImageOtherViewHolder(view, mListener)
        } else {
            val view = ItemMissionImageBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            QuestImageViewHolder(view, mListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestImageOtherViewHolder) {
            holder.bind(imageList[position])
        } else if (holder is QuestImageViewHolder) {
            holder.bind(imageList[position])
        }
    }

    override fun getItemCount(): Int {
        return imageList.count()
    }

    fun getItem(pos: Int) =
        imageList[pos]

    fun getList() =
        imageList

    fun syncData(_imageList: List<Uri>) {
        imageList = _imageList
        notifyDataSetChanged()
    }

    fun setOnImageItemListener(_listener: ImageItemListener) {
        mListener = _listener
    }
}