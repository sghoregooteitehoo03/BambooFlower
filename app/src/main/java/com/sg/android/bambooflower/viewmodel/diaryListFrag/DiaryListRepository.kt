package com.sg.android.bambooflower.viewmodel.diaryListFrag

import android.content.SharedPreferences
import androidx.paging.Pager
import androidx.paging.PagingConfig

import com.google.firebase.auth.FirebaseAuth
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject
import javax.inject.Named

class DiaryListRepository @Inject constructor(
    private val dao: DiaryDao,
    @Named(Contents.PREF_CHECK_DIARY_REWARD) private val checkDiaryPref: SharedPreferences,
    private val auth: FirebaseAuth
) {

    fun getAllDiaries() =
        Pager(PagingConfig(pageSize = 20)) {
            dao.getAllPagingDiaries(auth.currentUser?.uid)
        }

    fun getPref() =
        checkDiaryPref
}