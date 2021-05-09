package com.sg.android.bambooflower.data

data class Post(
    val title: String? = null,
    val contents: String? = null,
    val image: List<String>? = null,
    val viewCount: Int? = null,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = mutableMapOf(),
    val timeStamp: Long? = null,
    val uid: String? = null,
    val writer: String? = null,
    val profileImage: String? = null,
    val postPath: String? = null
)