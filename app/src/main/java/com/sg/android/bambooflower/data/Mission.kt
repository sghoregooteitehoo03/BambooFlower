package com.sg.android.bambooflower.data

data class Mission(
    val complete: MutableMap<String, Long> = mutableMapOf(),
    val document: String? = null,
    val level: Int? = null,
    val missionTitle: String? = null,
    val missionHow: String? = null,
    val missionImage: List<String>? = null,
    val uid: String? = null,
    val madeBy: String? = null,
    val profileImage: String = "",
    val timestamp: Long? = null,
) {
    override fun equals(other: Any?): Boolean {
        other as Mission
        return document == other.document
    }
}