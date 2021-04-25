package com.sg.android.bambooflower.data

data class User(
    val uid: String? = null,
    val name: String? = null,
    var profileImage: String? = null,
    val email: String? = null,
    val token: String? = null,
    var achievedCount: Int? = null,
    var achieved: Boolean? = null,
    val myLevel: Int? = null,
    var myMission: String? = null,
    var missionDoc: String? = null,
    val latestStart: Long? = null
) {
}