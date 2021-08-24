package com.sg.android.bambooflower.viewmodel.diaryFrag

import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.database.DiaryDao
import javax.inject.Inject

class DiaryRepository @Inject constructor(private val mDao: DiaryDao) {

    suspend fun deleteDiary(data: Diary) {
        mDao.deleteDiary(data)
    }
}