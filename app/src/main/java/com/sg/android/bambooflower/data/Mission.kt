package com.sg.android.bambooflower.data

data class Mission(
    val complete: MutableMap<String, Long> = mutableMapOf(),
    val document: String? = null,
    val level: Int? = null,
    val mission: String? = null
) {
}