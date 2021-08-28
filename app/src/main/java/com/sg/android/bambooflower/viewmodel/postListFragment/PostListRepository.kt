package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.adapter.paging.PostPagingSource
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class PostListRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {

    // 게시글 목록을 가져옴
    fun getPostList() =
        Pager(PagingConfig(10)) {
            PostPagingSource(functions, auth.currentUser?.uid!!)
        }.flow

    // 게시글 응원
    suspend fun pressedCheer(uid: String, postId: Int, questId: Int): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("postId", postId)
            put("questId", questId)
        }

        return functions.getHttpsCallable(Contents.FUNC_PRESSED_CHEER)
            .call(jsonData)
            .await()!!
    }

    // 게시글 삭제
    suspend fun deletePost(postData: Post): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("postId", postData.id)
            put("questId", postData.questId)
            put("uid", postData.userId)
        }

        // TODO: 나중에 수정
        return functions.getHttpsCallable("deletePostTest")
            .call(jsonData)
            .await()!!
    }
}