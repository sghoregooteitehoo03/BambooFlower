package com.sg.android.bambooflower.viewmodel.homeFrag

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.R
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {

    fun getHomeData() =
        functions.getHttpsCallable("getHomeDataTest")
            .call(auth.uid!!)
}