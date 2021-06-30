package com.sg.android.bambooflower.viewmodel.diaryListFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig

import com.google.firebase.auth.FirebaseAuth
import com.sg.android.bambooflower.data.database.DiaryDao
import javax.inject.Inject

class DiaryListRepository @Inject constructor(
    private val dao: DiaryDao,
    private val auth: FirebaseAuth
) {

    fun getAllDiaries() =
        Pager(PagingConfig(pageSize = 20)) {
            dao.getAllPagingDiaries(auth.currentUser?.uid)
        }
}