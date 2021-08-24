package com.sg.android.bambooflower.viewmodel.addDiaryFrag

import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.database.DiaryDao
import javax.inject.Inject

class AddDiaryRepository @Inject constructor(private val dao: DiaryDao) {
    suspend fun saveDiary(diaryData: Diary) {
        dao.insertDiary(diaryData)
    }

    suspend fun editDiary(diaryData: Diary) {
        dao.editDiary(diaryData)
    }
}