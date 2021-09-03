package com.sg.android.bambooflower.data

data class Shop(
    val id: Int,
    val price: Int,
    val image: String,
    val name: String
) {
    var isExists: Boolean = false
}