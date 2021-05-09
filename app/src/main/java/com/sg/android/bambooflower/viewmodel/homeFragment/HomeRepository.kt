package com.sg.android.bambooflower.viewmodel.homeFragment

import androidx.paging.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val dao: DiaryDao
) {
    fun getHomeData() =
        functions.getHttpsCallable(Contents.FUNC_GET_HOME_DATA)
            .call(auth.currentUser?.uid)

    suspend fun changeMission() =
        functions.getHttpsCallable(Contents.FUNC_CHANGE_MISSION)
            .call(auth.currentUser?.uid)
            .await()!!

    fun getAllDiaries() =
        Pager(PagingConfig(pageSize = 20)) {
            dao.getAllPagingDiaries(auth.currentUser?.uid)
        }
}