package com.sg.android.bambooflower.viewmodel.shopListFrag

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class ShopListRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    suspend fun getShopData(uid: String, pos: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("pos", pos)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_SHOP_DATA)
            .call(jsonData)
            .await()!!
    }

    suspend fun buyItem(uid: String, shopId: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("shopId", shopId)
        }

        return functions.getHttpsCallable(Contents.FUNC_BUY_SHOP_ITEM)
            .call(jsonData)
            .await()!!
    }
}