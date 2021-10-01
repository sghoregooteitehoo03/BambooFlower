package com.sg.android.bambooflower.data

import com.google.android.gms.ads.nativead.NativeAd

sealed class DiaryItemModel(val type: DataType) {
    data class Item(val diary: Diary) : DiaryItemModel(DataType.ITEM)
    data class Header(val nativeAd: NativeAd?) : DiaryItemModel(DataType.HEADER)
}