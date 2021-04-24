package com.sg.android.bambooflower.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.databinding.ItemRankingBinding

class RankingViewHolder(private val binding: ItemRankingBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(userData: User?, currentUserId: String?) {
        with(binding) {
            this.userData = userData
            this.currentUserId = currentUserId
            this.position = bindingAdapterPosition + 1
        }
    }
}