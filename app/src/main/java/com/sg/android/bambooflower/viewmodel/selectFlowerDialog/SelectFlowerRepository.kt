package com.sg.android.bambooflower.viewmodel.selectFlowerDialog

import android.util.Log
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class SelectFlowerRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {
    suspend fun getHavenFlowerData(uid: String): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("limit", 0)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_HAVEN_FLOWER_DATA)
            .call(jsonData)
            .await()!!
    }

    suspend fun selectFlower(uid: String, flowerName: String): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("flowerName", flowerName)
            put("flowerState", 1)
        }

        return functions.getHttpsCallable(Contents.FUNC_USER_SELECT_FLOWER)
            .call(jsonData)
            .await()!!
    }
}