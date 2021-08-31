package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.CollectionViewHolder
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.databinding.ItemCollectionFlowerBinding

class CollectionAdapter() : RecyclerView.Adapter<CollectionViewHolder>() {
    var flowerList = listOf<Flower>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
        val view = ItemCollectionFlowerBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {
        holder.bind(flowerList[position])
    }

    override fun getItemCount(): Int =
        flowerList.size

    fun syncData(_flowerList: List<Flower>) {
        flowerList = _flowerList
        notifyDataSetChanged()
    }
}