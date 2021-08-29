package com.sg.android.bambooflower.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Cheer
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class CheerPagingSource(
    private val functions: FirebaseFunctions,
    private val postId: Int
) : PagingSource<Int, Cheer>() {

    override fun getRefreshKey(state: PagingState<Int, Cheer>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Cheer> {
        return try {
            val currentKey = params.key ?: 0
            val jsonData = JSONObject().apply {
                put("offset", currentKey)
                put("postId", postId)
            }
            val result = functions.getHttpsCallable(Contents.FUNC_GET_CHEER_LIST)
                .call(jsonData)
                .await()!!
                .data as Map<*, *>

            if (result["cheerList"] == null) { // 오류 확인
                throw NullPointerException()
            }

            val cheerList = (result["cheerList"] as List<*>).map { cheer ->
                val jsonObject = JSONObject(cheer as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Cheer::class.java)
            }

            LoadResult.Page(
                data = cheerList,
                prevKey = null,
                nextKey = currentKey + 20
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}