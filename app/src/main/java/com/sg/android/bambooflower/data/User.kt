package com.sg.android.bambooflower.data

data class User(
    val uid: String,
    var name: String,
    var email: String,
    var profileImage: String? = null,
    val loginToken: String,
    var progress: Int,
    var money: Int,
    var questCount: Int,
    var flowerCount: Int,
    var flowerId: Int
) {
}