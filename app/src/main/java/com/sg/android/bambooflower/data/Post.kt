package com.sg.android.bambooflower.data

data class Post(
    val title: String? = null,
    val contents: String? = null,
    val image: Byte? = null,
    val viewCount: Int? = null,
    val favoriteCount: Int? = null,
    val favorites: MutableMap<String, Boolean> = mutableMapOf(),
    val timeStamp: Long? = null,
    val writer: String? = null
)