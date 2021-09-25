package com.sg.android.bambooflower.viewmodel.gardenFrag

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.data.Garden
import com.sg.android.bambooflower.data.database.GardenDao
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GardenRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val dao: GardenDao
) {

    // 서버로부터 인벤토리 정보읽어옴
    suspend fun getInventoryData(uid: String): HttpsCallableResult =
        functions.getHttpsCallable(Contents.FUNC_GET_INVENTORY_DATA)
            .call(uid)
            .await()!!

    // 정원 데이터 읽어오기
    fun loadGardenData() =
        dao.getGardenData()

    // 사용한 아이템 읽어오기
    suspend fun getUsedItemData() =
        dao.getUsedItemData()

    // 정원 데이터 저장
    suspend fun saveGardenData(data: MutableList<Garden>) {
        dao.clearGardenData()
        dao.insertGardenData(data)
    }
}