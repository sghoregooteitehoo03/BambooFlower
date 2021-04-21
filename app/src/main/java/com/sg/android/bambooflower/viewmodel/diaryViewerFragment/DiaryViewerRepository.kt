package com.sg.android.bambooflower.viewmodel.diaryViewerFragment

import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.database.DiaryDao
import javax.inject.Inject

class DiaryViewerRepository @Inject constructor(private val mDao: DiaryDao) {

    suspend fun deleteDiary(data: Diary) {
        mDao.deleteDiary(data)
    }
}