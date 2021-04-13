package com.sg.android.bambooflower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.viewholder.AlbumViewHolder
import com.sg.android.bambooflower.data.Album
import com.sg.android.bambooflower.databinding.ItemAlbumBinding

class AlbumAdapter : RecyclerView.Adapter<AlbumViewHolder>() {
    private var albumList = listOf<Album>()
    private lateinit var mListener: AlbumItemListener

    interface AlbumItemListener {
        fun onItemClickListener(pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlbumViewHolder(view, mListener)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(albumList[position])
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    fun setOnAlbumItemListener(_listener: AlbumItemListener) {
        mListener = _listener
    }

    fun syncData(_albumList: List<Album>) {
        albumList = _albumList
        notifyDataSetChanged()
    }

    fun getItem(pos: Int) =
        albumList[pos]
}