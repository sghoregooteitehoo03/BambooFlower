package com.sg.android.bambooflower.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sg.android.bambooflower.data.Diary

@Database(entities = [Diary::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): DiaryDao
}