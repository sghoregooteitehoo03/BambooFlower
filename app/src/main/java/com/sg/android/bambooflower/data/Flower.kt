package com.sg.android.bambooflower.data

data class Flower(
    val id: Int,
    val name: String,
    var state: Int,
    var image: String
) {
    var isSelected = false
    var isExists: Boolean = false
}