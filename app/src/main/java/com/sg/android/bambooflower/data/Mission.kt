package com.sg.android.bambooflower.data

data class Mission(
    val complete: MutableMap<String, Long> = mutableMapOf(),
    val document: String? = null,
    val level: Int? = null,
    val missionTitle: String? = null,
    val missionHow: String? = null
) {
    var isSelected = false
}