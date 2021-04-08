package com.sg.android.bambooflower.viewmodel.calendarDialog

import com.sg.android.bambooflower.data.database.DiaryDao
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

class CalendarRepository @Inject constructor(private val dao: DiaryDao) {
    suspend fun searchDiaryPosition(uid: String?, dateTime: Long): Int {
        return CoroutineScope(Dispatchers.IO).async {
            val diaryList = dao.getAllDiaries(uid)
            var position = 0
            var index = -1

            for (diary in diaryList) {
                val searchCalendar = Calendar.getInstance().apply {
                    timeInMillis = dateTime
                }
                val searchYear = searchCalendar.get(Calendar.YEAR)
                val searchMonth = searchCalendar.get(Calendar.MONTH)
                val searchDay = searchCalendar.get(Calendar.DAY_OF_MONTH)

                val diaryCalendar = Calendar.getInstance().apply {
                    timeInMillis = diary.timeStamp
                }
                val diaryYear = diaryCalendar.get(Calendar.YEAR)
                val diaryMonth = diaryCalendar.get(Calendar.MONTH)
                val diaryDay = diaryCalendar.get(Calendar.DAY_OF_MONTH)

                if (searchYear == diaryYear && searchMonth == diaryMonth && searchDay == diaryDay) {
                    index = position
                    break
                }

                position++
            }

            return@async index
        }.await()
    }
}