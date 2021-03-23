package com.sg.android.bambooflower.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sg.android.bambooflower.data.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM Diary")
    fun getAllDiaries(): PagingSource<Int, Diary>

    @Insert
    suspend fun insertDiary(data: Diary)
}