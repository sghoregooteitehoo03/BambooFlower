package com.sg.android.bambooflower.data

data class User(
    val uid: String? = null,
    val name: String? = null,
    var profileImage: String = "",
    val email: String? = null,
    var achievedCount: Int? = null,
    var achieveState: String? = null,
    val myLevel: Int? = null,
    var myMissionTitle: String? = null,
    var myMissionHow: String? = null,
    var missionDoc: String? = null,
    val latestStart: Long? = null
) {
    companion object {
        const val STATE_NOTHING = "STATE_NOTHING"
        const val STATE_LOADING = "STATE_LOADING"
        const val STATE_ALLOW = "STATE_ALLOW"
    }
}