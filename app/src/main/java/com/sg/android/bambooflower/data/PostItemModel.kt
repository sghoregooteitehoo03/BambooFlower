package com.sg.android.bambooflower.data

import com.google.android.gms.ads.nativead.NativeAd

sealed class PostItemModel(val type: DataType) {
    data class Item(val post: Post) : PostItemModel(DataType.ITEM)
    data class Header(val nativeAd: NativeAd?) : PostItemModel(DataType.HEADER)
}
