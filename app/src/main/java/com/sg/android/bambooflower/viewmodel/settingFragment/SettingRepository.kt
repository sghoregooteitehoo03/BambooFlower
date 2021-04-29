package com.sg.android.bambooflower.viewmodel.settingFragment

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dao: DiaryDao,
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    // 일기 모두삭제
    suspend fun clearDiary(uid: String?) {
        dao.clearDiary(uid)
    }

    // 계정 탈퇴
    suspend fun deleteAccount(user: User) {
        val uid = user.uid!!

        // 유저 데이터 삭제
        store.collection(Contents.COLLECTION_USER)
            .document(uid)
            .delete()
            .await()

        // 유저 프로필 사진 삭제
        if (user.profileImage != null) {
            storage.reference.child(uid)
                .child("profile.png")
                .delete()
                .await()
        }

        // 유저의 게시글 및 사진 삭제
        val postList = store.collection(Contents.COLLECTION_POST)
            .whereEqualTo("uid", uid)
            .get()
            .await()

        postList.forEach {
            val postData = it.toObject(Post::class.java)

            // 게시글에 사진들을 차례로 삭제함
            for (i in postData.image!!.indices) {
                val imageName = "$i.png"
                val reference = storage.reference
                    .child(uid)
                    .child(Contents.CHILD_POST_IMAGE)
                    .child(postData.postPath!!)
                    .child(imageName)

                reference.delete()
                    .await()
            }

            // 게시글 데이터 삭제
            it.reference.delete()
                .await()
        }

        // 계정을 탈퇴 함
        with(auth) {
            signInWithCustomToken(user.token!!)
            currentUser?.delete()
                ?.await()
        }
    }
}