package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.messaging.FirebaseMessaging
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class EmailLoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val messaging: FirebaseMessaging
) {
    suspend fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)
            .await()!!

    suspend fun checkUserData(): HttpsCallableResult {
        val uid = auth.currentUser?.uid
        val token = messaging.token
            .await()!!
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("alarmToken", token)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_USER_DATA)
            .call(jsonData)
            .await()!!
    }
}