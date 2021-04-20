package com.sg.android.bambooflower.viewmodel.diaryWriteFragment

import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.database.DiaryDao
import javax.inject.Inject

class DiaryWriteRepository @Inject constructor(private val dao: DiaryDao) {
    suspend fun saveDiary(diaryData: Diary) {
        dao.insertDiary(diaryData)
    }
}