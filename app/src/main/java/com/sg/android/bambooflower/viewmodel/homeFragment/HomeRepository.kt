package com.sg.android.bambooflower.viewmodel.homeFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val dao: DiaryDao
) {
    fun getHomeData() =
        functions.getHttpsCallable(Contents.FUNC_GET_HOME_DATA)
            .call(auth.currentUser?.uid)

    fun successMission() =
        functions.getHttpsCallable(Contents.FUNC_SUCCESS_MISSION)
            .call(auth.currentUser?.uid)

    fun changeMission() =
        functions.getHttpsCallable(Contents.FUNC_CHANGE_MISSION)
            .call(auth.currentUser?.uid)

    fun getAllDiaries() =
        Pager(PagingConfig(pageSize = 20)) {
            dao.getAllPagingDiaries(auth.currentUser?.uid)
        }
}