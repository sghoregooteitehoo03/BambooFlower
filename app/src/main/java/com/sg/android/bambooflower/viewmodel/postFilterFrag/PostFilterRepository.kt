package com.sg.android.bambooflower.viewmodel.postFilterFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.sg.android.bambooflower.adapter.paging.MyPostPagingSource
import com.sg.android.bambooflower.adapter.paging.PostFilterPagingSource
import com.sg.android.bambooflower.adapter.paging.PostPagingSource
import com.sg.android.bambooflower.data.Accept
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class PostFilterRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions
) {
    fun getPostList() =
        Pager(PagingConfig(20)) {
            PostPagingSource(store)
        }

    fun getPostFilterList() =
        Pager(PagingConfig(20)) {
            PostFilterPagingSource(store)
        }

    fun getMyPostList(uid: String) =
        Pager(PagingConfig(20)) {
            MyPostPagingSource(store, uid)
        }

    // 응원하기
    suspend fun favorite(userData: User, postData: Post) {
        val postRef = store.collection(Contents.COLLECTION_POST)
            .document(postData.postPath!!) // 게시글 문서
        val acceptRef = store.collection(Contents.COLLECTION_ACCEPT)
            .document() // 인정한 사람 문서

//        store.runTransaction { transition ->
//            val currentData = transition[postRef].toObject(Post::class.java) // 해당 게시글 데이터
//            val acceptData = Accept(
//                userData.uid,
//                userData.name,
//                userData.profileImage,
//                postData.postPath,
//                System.currentTimeMillis()
//            ) // 인정한 사람 데이터
//
//            if (currentData != null) {
//                currentData.favoriteCount += 1 // 미션 인정 카운트
//                currentData.favorites[userData.uid!!] = true // 누른 사람들 표시
//
//                postData.favoriteCount = currentData.favoriteCount
//                postData.favorites = currentData.favorites
//
//                transition.update( // 게시글 정보 업데이트
//                    postRef,
//                    "favorites",
//                    currentData.favorites,
//                    "favoriteCount",
//                    postData.favoriteCount
//                )
//                transition.set(acceptRef, acceptData) // 인정한 사람 데이터 저장
//            }
//        }.await()
    }

    // 게시글 삭제
    suspend fun deletePost(postData: Post) {
        val postPath = postData.postPath!!
        val uid = postData.uid!!

        // storage 이미지 삭제
        val imageName = "${postData.timeStamp}.png"
        val reference = storage.reference
            .child(uid)
            .child(Contents.CHILD_POST_IMAGE)
            .child(postPath)
            .child(imageName)

        reference.delete()
            .await()

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