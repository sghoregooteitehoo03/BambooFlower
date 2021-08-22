package com.sg.android.bambooflower.data

data class UsersQuest(
    val quest: Quest,
    val state: Int,
    val timestamp: Int
) {
    companion object {
        const val STATE_NOTHING = 1
        const val STATE_LOADING = 2
        const val STATE_COMPLETE_WITH_REWARD = 3
        const val STATE_COMPLETE = 4
    }
}
