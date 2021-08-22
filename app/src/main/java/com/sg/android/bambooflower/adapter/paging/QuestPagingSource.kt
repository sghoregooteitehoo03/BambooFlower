package com.sg.android.bambooflower.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Quest
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject

class QuestPagingSource(private val functions: FirebaseFunctions) :
    PagingSource<Int, Quest>() {

    override fun getRefreshKey(state: PagingState<Int, Quest>): Int ? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Quest> {
        return try {
            val currentKey = params.key ?: 0
            val result = functions.getHttpsCallable(Contents.FUNC_GET_QUEST_LIST)
                .call(currentKey)
                .await()!!
                .data as Map<*, *>

            if (result["quests"] == null) { // 오류 확인
                throw NullPointerException()
            }

            val questList = (result["quests"] as List<*>).map { quest ->
                val jsonObject = JSONObject(quest as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Quest::class.java)
            }

            LoadResult.Page(
                data = questList,
                prevKey = null,
                nextKey = currentKey + 15
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}