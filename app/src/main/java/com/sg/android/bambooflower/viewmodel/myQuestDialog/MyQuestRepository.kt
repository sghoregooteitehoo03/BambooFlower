package com.sg.android.bambooflower.viewmodel.myQuestDialog

import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MyQuestRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    // 퀘스트 포기
    suspend fun giveUpQuest(id: Int) =
        functions.getHttpsCallable(Contents.FUNC_GIVE_UP_QUEST)
            .call(id)
            .await()!!
}