package com.sg.android.bambooflower.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Diary(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "contents") val contents: String,
    @ColumnInfo(name = "timeStamp") val timeStamp: Long,
    @ColumnInfo(name = "uid") val uid: String
) {
}