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

    suspend fun acceptQuest(uid: String, questId: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("questId", questId)
        }

        return functions.getHttpsCallable(Contents.FUNC_ACCEPT_QUEST)
            .call(jsonData)
            .await()!!
    }

    suspend fun giveUpQuest(id: Int) =
        functions.getHttpsCallable(Contents.FUNC_GIVE_UP_QUEST)
            .call(id)
            .await()!!
}