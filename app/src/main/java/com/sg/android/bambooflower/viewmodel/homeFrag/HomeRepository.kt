package com.sg.android.bambooflower.viewmodel.homeFrag

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {

    fun getHomeData() =
        functions.getHttpsCallable("getHomeDataTest")
            .call(auth.uid!!)

    // 꽃 -> 씨앗으로 바꿈
    suspend fun giveUpFlower(uid: String): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("flowerName", "씨앗")
            put("flowerState", 0)
        }

        return functions.getHttpsCallable(Contents.FUNC_USER_SELECT_FLOWER)
            .call(jsonData)
            .await()!!
    }
}