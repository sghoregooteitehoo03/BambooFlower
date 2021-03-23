package com.sg.android.bambooflower.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sg.android.bambooflower.data.Diary

@Database(entities = [Diary::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DiaryDao
}