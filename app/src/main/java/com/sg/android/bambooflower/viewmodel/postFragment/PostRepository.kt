package com.sg.android.bambooflower.viewmodel.postFragment

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Accept
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions
) {

    // 게시글 읽어오기
    fun getPostData(doc: String) =
        store.collection(Contents.COLLECTION_POST)
            .document(doc)
            .get()

    // 응원하기
    suspend fun cheerUp(userData: User, postData: Post): Boolean {
        val postRef = store.collection(Contents.COLLECTION_POST)
            .document(postData.postPath!!) // 게시글 문서
        val acceptRef = store.collection(Contents.COLLECTION_ACCEPT)
            .document() // 인정한 사람 문서
        var isCheerUp = false

        store.runTransaction { transition ->
            val currentData = transition[postRef].toObject(Post::class.java) // 해당 게시글 데이터
            val acceptData = Accept(
                userData.uid,
                userData.name,
                userData.profileImage,
                postData.postPath,
                System.currentTimeMillis()
            ) // 인정한 사람 데이터

            if (currentData != null) {
                isCheerUp = true

                currentData.favoriteCount += 1 // 미션 인정 카운트
                currentData.favorites[userData.uid!!] = true // 누른 사람들 표시

                postData.favoriteCount = currentData.favoriteCount
                postData.favorites = currentData.favorites

                transition.update( // 게시글 정보 업데이트
                    postRef,
                    "favorites",
                    currentData.favorites,
                    "favoriteCount",
                    postData.favoriteCount
                )
                transition.set(acceptRef, acceptData) // 인정한 사람 데이터 저장
            }
        }.await()

        return isCheerUp
    }

    // 게시글 삭제
    suspend fun deletePost(postData: Post) {
        val postPath = postData.postPath!!
        val uid = postData.uid!!

        for (i in postData.image!!.indices) { // storage에 저장된 이미지를 차례대로 삭제함
            val imageName = "$i.png"
            val reference = storage.reference
                .child(uid)
                .child(Contents.CHILD_POST_IMAGE)
                .child(postPath)
                .child(imageName)

            reference.delete()
                .await()
        }

        // 나머지 작업들은 Functions에서 처리
        val jsonData = JSONObject().apply {
            put("postData", Gson().toJson(postData))
            put("uid", uid)
        }

        functions.getHttpsCallable(Contents.FUNC_DELETE_POST)
            .call(jsonData)
            .await()
    }
}