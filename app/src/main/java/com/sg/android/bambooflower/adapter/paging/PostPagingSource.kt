package com.sg.android.bambooflower.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class PostPagingSource(
    private val functions: FirebaseFunctions,
    private val uid: String
) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val currentKey = params.key ?: 0
            val jsonData = JSONObject().apply {
                put("offset", currentKey)
                put("uid", uid)
            }
            val result = functions.getHttpsCallable(Contents.FUNC_GET_POST_LIST)
                .call(jsonData)
                .await()!!
                .data as Map<*, *>

            if (result["posts"] == null) { // 오류 확인
                throw NullPointerException()
            }

            val postList = (result["posts"] as List<*>).map { post ->
                val jsonObject = JSONObject(post as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Post::class.java)
            }

            LoadResult.Page(
                data = postList,
                prevKey = null,
                nextKey = currentKey + 10
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}