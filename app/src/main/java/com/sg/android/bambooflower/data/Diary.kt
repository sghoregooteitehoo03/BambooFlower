package com.sg.android.bambooflower.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "contents") val contents: String,
    @ColumnInfo(name = "progress") val progress: Int,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long,
    @ColumnInfo(name = "flowerImage") val flowerImage: String,
    @ColumnInfo(name = "flowerId") val flowerId: Int,
    @ColumnInfo(name = "userId") val userId: String
) {
}