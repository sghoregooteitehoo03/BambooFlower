package com.sg.android.bambooflower.data

data class Inventory(
    val id: Int?,
    val itemName: String,
    val itemIcon: String,
    val itemImage: String,
    val flatImage: String,
    var itemCount: Int,
    val category: Int,
) {
    var isUsing = false

    companion object {
        const val ITEM_CATEGORY_FLOWER = 0
        const val ITEM_CATEGORY_ACCESSORY = 1
        const val ITEM_CATEGORY_WALLPAPER = 2
    }

    override fun equals(other: Any?): Boolean {
        other as Inventory
        return id == other.id && category == other.category
    }
}