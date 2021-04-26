package com.sg.android.bambooflower.viewmodel.profileFragment

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    // 프로필 이미지 변경
    suspend fun changeProfileImage(user: User, imageUri: Uri) {
        val uid = user.uid!!

        val imageName = "profile.png"
        val storeRef = store.collection(Contents.COLLECTION_USER)
            .document(uid)
        val storageRef = storage.reference
            .child(uid)
            .child(imageName)

        storageRef.putFile(imageUri) // storage에 이미지 추가
            .await()

        user.profileImage = storageRef.downloadUrl // 프로필 이미지 변경
            .await()
            .toString()

        store.runTransaction { transition -> // store에 이미지 추가
            transition.set(storeRef, user)
        }.await()
    }

    // 로그아웃
    fun signOut() {
        auth.signOut()
    }
}