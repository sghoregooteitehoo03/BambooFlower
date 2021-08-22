package com.sg.android.bambooflower.viewmodel.homeFrag

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Named

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    @Named(Contents.PREF_CHECK_FIRST) private val checkPref: SharedPreferences
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

    fun checkFirst() {
        if (checkPref.getBoolean(Contents.PREF_KEY_IS_FIRST, true)) {
            with(checkPref.edit()) {
                putBoolean(com.sg.android.bambooflower.other.Contents.PREF_KEY_IS_FIRST, false)
                commit()
            }
        }
    }
}