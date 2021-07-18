package com.sg.android.bambooflower.data

data class Mission(
    val complete: MutableMap<String, Long> = mutableMapOf(),
    val document: String? = null,
    val level: Int? = null,
    val missionTitle: String? = null,
    val missionHow: String? = null,
    val missionImage: List<String> = listOf(),
    val uid: String? = null,
    val madeBy: String? = null,
    val profileImage: String = ""
) {
    override fun equals(other: Any?): Boolean {
        other as Mission
        return document == other.document
    }
}