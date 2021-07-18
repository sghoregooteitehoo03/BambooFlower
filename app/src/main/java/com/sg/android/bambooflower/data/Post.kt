package com.sg.android.bambooflower.data

data class Post(
    val title: String? = null,
    val missionDoc: String? = null,
    val contents: String? = null,
    val image: String? = null,
    var favoriteCount: Int = 0,
    var favorites: MutableMap<String, Boolean> = mutableMapOf(),
    val timeStamp: Long? = null,
    val uid: String? = null,
    val writer: String? = null,
    val profileImage: String? = null,
    val postPath: String? = null
)