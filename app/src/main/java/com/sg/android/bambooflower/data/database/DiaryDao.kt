package com.sg.android.bambooflower.data.database

import androidx.paging.PagingSource
import androidx.room.*
import com.sg.android.bambooflower.data.Diary

@Dao
interface DiaryDao {
    @Query("SELECT * FROM Diary WHERE userId == :uid ORDER BY timeStamp DESC")
    fun getAllPagingDiaries(uid: String?): PagingSource<Int, Diary>

    @Query("SELECT * FROM Diary WHERE userId == :uid ORDER BY timeStamp DESC")
    fun getAllDiaries(uid: String?): List<Diary>

    @Insert
    suspend fun insertDiary(data: Diary)

    @Delete
    suspend fun deleteDiary(data: Diary)

    @Update
    suspend fun editDiary(data: Diary)

    @Query("DELETE FROM Diary WHERE userId == :uid")
    suspend fun clearDiary(uid: String?)
}