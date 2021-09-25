package com.sg.android.bambooflower.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Garden

@Database(entities = [Diary::class, Garden::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDiaryDao(): DiaryDao
    abstract fun getGardenDao(): GardenDao
}