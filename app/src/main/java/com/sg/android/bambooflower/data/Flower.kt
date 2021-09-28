package com.sg.android.bambooflower.data

data class Flower(
    val id: Int,
    val name: String,
    val state: Int,
    val image: String
) {
    var isSelected = false
}