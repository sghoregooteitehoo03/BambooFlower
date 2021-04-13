package com.sg.android.bambooflower.data

import android.net.Uri

data class Album(
    val imageUri: Uri,
    var isChecked: Boolean = false
) {
}