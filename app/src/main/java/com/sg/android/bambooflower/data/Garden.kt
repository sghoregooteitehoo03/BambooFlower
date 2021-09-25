package com.sg.android.bambooflower.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Garden(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "itemId")
    val itemId: Int,
    @ColumnInfo(name = "itemImage")
    val itemImage: String,
    @ColumnInfo(name = "category")
    val category: Int,
    @ColumnInfo(name = "collocatedPos")
    var collocatedPos: Int,
    @ColumnInfo(name = "collocatedX")
    var collocatedX: Float,
    @ColumnInfo(name = "collocatedY")
    var collocatedY: Float
) {
    override fun equals(other: Any?): Boolean {
        other as Garden
        return (category == other.category && collocatedPos == other.collocatedPos)
    }
}