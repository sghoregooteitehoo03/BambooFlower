package com.sg.android.bambooflower.data

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long,
    val image: String,
    var isCheer: Boolean,
    var cheerCount: Int,
    val userId: String,
    val userName: String,
    val userImage: String,
    val questId: Int
)