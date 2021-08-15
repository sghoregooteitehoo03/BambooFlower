package com.sg.android.bambooflower.data

import android.graphics.Bitmap

data class User(
    val uid: String,
    var name: String,
    var email: String,
    var profileImage: Bitmap? = null,
    val loginToken: String,
    var startedTime: Long,
    var progress: Int,
    var money: Int,
    var flowerId: Int
) {
}