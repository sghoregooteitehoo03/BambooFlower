package com.sg.android.bambooflower.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sg.android.bambooflower.data.Garden
import com.sg.android.bambooflower.data.UsedItem

@Dao
interface GardenDao {
    @Query("SELECT * FROM Garden")
    fun getGardenData(): LiveData<MutableList<Garden>>

    @Query("SELECT itemId, COUNT(*) AS itemCount, category FROM Garden GROUP BY itemId")
    suspend fun getUsedItemData(): List<UsedItem>

    @Query("DELETE FROM Garden")
    suspend fun clearGardenData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGardenData(data: MutableList<Garden>)
}