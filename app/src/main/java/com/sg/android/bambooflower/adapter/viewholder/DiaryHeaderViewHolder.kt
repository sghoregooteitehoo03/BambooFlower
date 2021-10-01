package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.sg.android.bambooflower.databinding.ItemDiaryHeaderBinding

class DiaryHeaderViewHolder(
    private val binding: ItemDiaryHeaderBinding,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(data: NativeAd?) {
        with(binding.adLayout) {
            iconView = binding.adImage
            headlineView = binding.adHeadline
            bodyView = binding.adBody
            callToActionView = binding.adBtn
        }
        binding.nativeAd = data
    }
}