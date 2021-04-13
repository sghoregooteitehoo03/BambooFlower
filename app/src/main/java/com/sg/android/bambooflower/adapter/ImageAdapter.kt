package com.sg.android.bambooflower.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.ImageViewHolder
import com.sg.android.bambooflower.databinding.ItemImageBinding

class ImageAdapter() : RecyclerView.Adapter<ImageViewHolder>() {
    private var imageList = mutableListOf<Uri>()
    private lateinit var mListener: ImageItemListener

    interface ImageItemListener {
        fun onRemoveListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.count()
    }

    fun syncData(_imageList: MutableList<Uri>) {
        imageList = _imageList
        notifyDataSetChanged()
    }

    fun setOnImageItemListener(_listener: ImageItemListener) {
        mListener = _listener
    }
}