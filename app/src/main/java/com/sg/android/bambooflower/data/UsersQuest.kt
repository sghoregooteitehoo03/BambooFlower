package com.sg.android.bambooflower.data

data class UsersQuest(
    var id: Int,
    val quest: Quest,
    var state: Int,
    var timestamp: Int
) {
    companion object {
        const val STATE_NOTHING = 1
        const val STATE_LOADING = 2
        const val STATE_COMPLETE_WITH_REWARD = 3
        const val STATE_COMPLETE = 4
    }

    override fun equals(other: Any?): Boolean {
        other as UsersQuest
        return id == other.id
    }
}
