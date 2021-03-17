package com.sg.android.bambooflower.data

data class User(
    val name: String? = null,
    val token: String? = null,
    var achievedCount: Int? = null,
    var isAchieved: Boolean? = null,
    val myLevel: Int? = null,
    val myMission: String? = null,
    val missionDoc: String? = null,
    val latestStart: Long? = null
) {
}