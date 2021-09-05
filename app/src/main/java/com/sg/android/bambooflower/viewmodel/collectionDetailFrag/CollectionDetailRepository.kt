package com.sg.android.bambooflower.viewmodel.collectionDetailFrag

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class CollectionDetailRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    suspend fun getInventoryData(uid: String): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("tableName", "flower")
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_INVENTORY_DATA)
            .call(jsonData)
            .await()!!
    }
}