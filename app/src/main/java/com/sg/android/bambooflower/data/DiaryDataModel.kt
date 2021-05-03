package com.sg.android.bambooflower.data

sealed class DiaryDataModel(val type: DataType) {
    data class Item(val diary: Diary) : DiaryDataModel(DataType.ITEM)
    object Header : DiaryDataModel(DataType.HEADER)
}