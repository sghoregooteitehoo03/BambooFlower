package com.sg.android.bambooflower.viewmodel.profileFragment

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions
) {

    // 프로필 이미지 변경
    suspend fun changeProfileImage(user: User, imageUri: Uri) {
        val uid = user.uid!!

        val imageName = "profile.png"
        val storageRef = storage.reference
            .child(uid)
            .child(imageName)

        storageRef.putFile(imageUri) // storage에 이미지 추가
            .await()

        // store에 저장된 프로필 이미지들을 functions에서 다 수정함
        val profileImage = storageRef.downloadUrl
            .await()
            .toString()
        val jsonObj = JSONObject().apply {
            put("uid", uid)
            put("profileImage", profileImage)
        }

        functions.getHttpsCallable(Contents.FUNC_CHANGE_PROFILE)
            .call(jsonObj)
            .await()

        user.profileImage = profileImage
    }

    // 로그아웃
    fun signOut() {
        auth.signOut()
    }
}