package com.sg.android.bambooflower.viewmodel.profileFragment

import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    // 프로필 데이터 가져오기
    suspend fun getProfileData(uid: String) =
        functions.getHttpsCallable(Contents.FUNC_GET_PROFILE_DATA)
            .call(uid)
            .await()!!
}