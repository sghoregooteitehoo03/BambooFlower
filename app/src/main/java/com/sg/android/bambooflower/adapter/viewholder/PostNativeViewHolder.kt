package com.sg.android.bambooflower.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.sg.android.bambooflower.databinding.ItemPostNativeBinding

class PostNativeViewHolder(
    private val binding: ItemPostNativeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: NativeAd?) {
        with(binding.adLayout) {
            iconView = binding.adImage
            headlineView = binding.adHeadline
            bodyView = binding.adBody
            mediaView = binding.adMedia
            callToActionView = binding.adBtn
        }
        binding.nativeAd = data
    }
}