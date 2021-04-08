package com.sg.android.bambooflower.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sg.android.bambooflower.data.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM Diary WHERE uid == :uid ORDER BY timeStamp DESC")
    fun getAllPagingDiaries(uid: String?): PagingSource<Int, Diary>

    @Query("SELECT * FROM Diary WHERE uid == :uid ORDER BY timeStamp DESC")
    fun getAllDiaries(uid: String?): List<Diary>

    @Insert
    suspend fun insertDiary(data: Diary)
}