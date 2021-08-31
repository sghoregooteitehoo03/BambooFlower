package com.sg.android.bambooflower.viewmodel.editProfileFrag

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class EditProfileRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    // 프로필 변경
    suspend fun changeProfile(
        uid: String,
        profileImage: String,
        email: String,
        name: String
    ): HttpsCallableResult {
        val jsonObj = JSONObject().apply {
            put("uid", uid)
            put("profileImage", profileImage)
            put("email", email)
            put("name", name)
        }

        // TODO: 나중에 수정
        return functions.getHttpsCallable("changeProfileTest")
            .call(jsonObj)
            .await()!!
    }
}