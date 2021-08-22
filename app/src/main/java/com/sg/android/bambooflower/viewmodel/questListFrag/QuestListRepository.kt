package com.sg.android.bambooflower.viewmodel.questListFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.adapter.paging.QuestPagingSource
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestListRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {
    // 유저의 퀘스트를 가져옴
    suspend fun getUsersQuest(uid: String) =
        functions.getHttpsCallable(Contents.FUNC_GET_USERS_QUEST)
            .call(uid)
            .await()!!

    // 모든 퀘스트 목록을 가져옴
    fun getQuestList() =
        Pager(PagingConfig(15)) {
            QuestPagingSource(functions)
        }.flow
}