package com.sg.android.bambooflower.data

data class User(
    val uid: String? = null,
    val name: String? = null,
    val token: String? = null,
    var achievedCount: Int? = null,
    var isAchieved: Boolean? = null,
    val myLevel: Int? = null,
    var myMission: String? = null,
    var missionDoc: String? = null,
    val latestStart: Long? = null
) {
}