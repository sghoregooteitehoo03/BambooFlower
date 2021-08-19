package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.SelectFlowerViewHolder
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.databinding.ItemSelectFlowerBinding

class SelectFlowerAdapter : RecyclerView.Adapter<SelectFlowerViewHolder>() {
    private var flowerList = listOf<Flower>()
    private lateinit var mListener: FlowerItemListener

    interface FlowerItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectFlowerViewHolder {
        val view =
            ItemSelectFlowerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectFlowerViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: SelectFlowerViewHolder, position: Int) {
        holder.bind(flowerList[position])
    }

    override fun getItemCount() =
        flowerList.size

    fun syncData(_flowerList: List<Flower>) {
        flowerList = _flowerList
        notifyDataSetChanged()
    }

    fun setOnItemListener(_listener: FlowerItemListener) {
        mListener = _listener
    }

    fun getData(pos: Int) =
        flowerList[pos]
}