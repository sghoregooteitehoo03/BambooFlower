package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.PagerImageViewHolder
import com.sg.android.bambooflower.databinding.PagerImageBinding

class ImagePagerAdapter(private val imageList: List<String>, private val hasOptions: Boolean = false) :
    RecyclerView.Adapter<PagerImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerImageViewHolder {
        val view = PagerImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagerImageViewHolder, position: Int) {
        holder.bind(imageList[position].toUri(), hasOptions)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}