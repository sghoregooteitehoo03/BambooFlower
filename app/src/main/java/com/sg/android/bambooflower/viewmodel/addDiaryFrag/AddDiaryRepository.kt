package com.sg.android.bambooflower.viewmodel.addDiaryFrag

import android.content.SharedPreferences
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class AddDiaryRepository @Inject constructor(
    private val dao: DiaryDao,
    @Named(Contents.PREF_CHECK_DIARY_REWARD) private val checkDiaryPref: SharedPreferences
) {
    suspend fun saveDiary(diaryData: Diary) {
        setPref(diaryData.timeStamp)
        dao.insertDiary(diaryData)
    }

    suspend fun editDiary(diaryData: Diary) {
        dao.editDiary(diaryData)
    }

    private fun setPref(timestamp: Long) {
        val prefKey = SimpleDateFormat("yyyyMMdd", Locale.KOREA).format(timestamp)
        if (checkDiaryPref.getInt(prefKey, -1) == -1) { // 일기를 오늘 처음 작성하는 경우
            with(checkDiaryPref.edit()) {
                clear() // 기존에 있던 데이터 모두 삭제

                putInt(prefKey, 1) // 보상을 받을 수 있는 값으로 수정
                commit()
            }
        }
    }
}