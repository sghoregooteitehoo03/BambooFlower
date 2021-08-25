package com.sg.android.bambooflower.viewmodel.flowerStateDialog

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class FlowerStateRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {
    // 보상 받기
    suspend fun getReward(
        uid: String,
        progress: Int,
        money: Int,
        flower: Flower,
        usersQuestId: Int
    ): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("progress", progress)
            put("money", money)
            put("flowerName", flower.name)
            put("flowerState", flower.state)
            put("usersQuestId", usersQuestId)
        }

        return functions.getHttpsCallable(Contents.FUNC_UPDATE_USER_PROGRESS)
            .call(jsonData)
            .await()!!
    }
}