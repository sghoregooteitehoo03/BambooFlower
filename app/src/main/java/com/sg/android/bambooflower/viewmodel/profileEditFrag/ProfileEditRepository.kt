package com.sg.android.bambooflower.viewmodel.profileEditFrag

import androidx.core.net.toUri
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class ProfileEditRepository @Inject constructor(
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions
) {

    // 프로필 변경
    suspend fun changeProfile(
        user: User,
        profileImage: String,
        email: String,
        name: String
    ) {
        val uid = user.uid!!

        // storage에 이미지 추가
        val downloadImage = if (user.profileImage == profileImage) {
            // 같은 이미지 일 경우
            profileImage
        } else if (profileImage.isNotEmpty()) {
            // 다른 이미지일 경우
            val imageName = "profile.png"
            val storageRef = storage.reference
                .child(uid)
                .child(imageName)

            storageRef.putFile(profileImage.toUri()) // storage에 이미지 추가
                .await()

            storageRef.downloadUrl
                .await()
                .toString()
        } else {
            // 이미지가 없는 경우
            ""
        }

        val jsonObj = JSONObject().apply {
            put("uid", uid)
            put("profileImage", downloadImage)
            put("email", email)
            put("name", name)
        }

        functions.getHttpsCallable(Contents.FUNC_CHANGE_PROFILE)
            .call(jsonObj)
            .await()
    }
}