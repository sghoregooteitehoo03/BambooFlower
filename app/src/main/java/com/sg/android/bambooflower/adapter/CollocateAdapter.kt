package com.sg.android.bambooflower.adapter

import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.CollocateViewHolder
import com.sg.android.bambooflower.databinding.ItemCollocateBinding

class CollocateAdapter() : RecyclerView.Adapter<CollocateViewHolder>() {
    private var collocatedList = MutableList(25) { false } // 아이템이 배치된 위치
    private var showTileList = MutableList(25) { false } // 타일을 보여줄 위치
    private var previousPos = -1

    private lateinit var listener: CollocateItemListener

    interface CollocateItemListener {
        fun onItemDragListener(view: View, event: DragEvent, pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollocateViewHolder {
        val view =
            ItemCollocateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollocateViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: CollocateViewHolder, position: Int) {
        holder.bind(showTileList[position], collocatedList[position])
    }

    override fun getItemCount() =
        showTileList.size

    fun setOnItemListener(_listener: CollocateItemListener) {
        listener = _listener
    }

    fun setTileList(pos: Int, isShowing: Boolean) {
        if (pos != -1) {
            showTileList[pos] = isShowing
            notifyItemChanged(pos)
        } else {
            showTileList[previousPos] = isShowing
            notifyItemChanged(previousPos)
        }

        previousPos = pos
    }

    fun setCollocatedList(pos: Int, isCollocated: Boolean) {
        collocatedList[pos] = isCollocated
    }

    fun isCollocated(pos: Int) =
        collocatedList[pos]
}