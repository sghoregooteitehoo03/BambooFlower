package com.sg.android.bambooflower.viewmodel.questDialog

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    // 퀘스트 정보 가져옴
    suspend fun getUsersQuest(uid: String, questId: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("questId", questId)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_QUEST_DATA)
            .call(jsonData)
            .await()!!
    }

    // 퀘스트 수락
    suspend fun acceptQuest(uid: String, questId: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("questId", questId)
        }

        return functions.getHttpsCallable(Contents.FUNC_ACCEPT_QUEST)
            .call(jsonData)
            .await()!!
    }
}