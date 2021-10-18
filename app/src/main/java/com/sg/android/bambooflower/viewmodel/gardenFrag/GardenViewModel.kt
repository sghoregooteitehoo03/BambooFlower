package com.sg.android.bambooflower.viewmodel.gardenFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Inventory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class GardenViewModel @Inject constructor(
    private val repository: GardenRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _inventoryList = MutableLiveData<List<Inventory>>(null) // 유저의 인벤토리 리스트
    private val _filterList = MutableLiveData<List<Inventory>>() // 필터링 된 인벤토리 리스트

    val isLoading: LiveData<Boolean> = _isLoading
    val filterList: LiveData<List<Inventory>> = _filterList
    val savedGardenList = repository.loadGardenData() // 정원 데이터

    val isExpand = MutableLiveData(false) // 확장 여부
    val isEdited = MutableLiveData(false) // 수정 여부
    val isEnteredInventory = MutableLiveData(false) // 배치된 아이템 인벤토리 진입여부
    val isError = MutableLiveData(false) // 오류 여부
    val category = MutableLiveData(Inventory.ITEM_CATEGORY_FLOWER) // 인벤토리 카테고리
    val wallpaperData = MutableLiveData<String>(null) // 정원 벽지 데이터
    val flatImageData = MutableLiveData<String>(null) // 평지 이미지 데이터

    // 인벤토리 가져오기
    fun changeInventoryFilter(category: Int) = viewModelScope.launch {
        _inventoryList.value?.let { list ->
            _filterList.value = list.filter {
                it.category == category
            }
        }
    }

    // 서버로 부터 인벤토리 정보를 읽어옴
    fun getInventoryFromServer(uid: String) = viewModelScope.launch {
        try {
            val result = repository.getInventoryData(uid)
                .data as Map<*, *>
            val usedItem = repository.getUsedItemData() // 사용한 아이템

            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            _inventoryList.value = (result["inventory"] as List<*>).map { inventory ->
                val jsonObject = JSONObject(inventory as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Inventory::class.java)
            }.apply {
                // 유저의 인벤토리와 유저가 사용한 아이템을 동기화 시킴
                usedItem.forEach { usedItem ->
                    val idx = this
                        .indexOf(
                            Inventory(
                                usedItem.itemId,
                                "",
                                "",
                                "",
                                "",
                                0,
                                usedItem.category
                            )
                        )

                    if (this[idx].category != Inventory.ITEM_CATEGORY_WALLPAPER) {
                        // 사용한 아이템의 카테고리가 벽지가 아닐때
                        this[idx].itemCount -= usedItem.itemCount
                    } else {
                        // 사용한 아이템의 카테고리가 벽지일 때
                        this[idx].isUsing = true
                    }
                }
            }

            // 카테고리를 기준으로 인벤토리 필터링
            _filterList.value = _inventoryList.value
                ?.filter {
                    it.category == category.value!!
                }
            _isLoading.value = false // 로딩 끝
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }
    }

    fun getItemWithIdAndTag(id: Int, category: Int): Inventory {
        val idx = _inventoryList.value!!.indexOf(
            Inventory(
                id,
                "",
                "",
                "",
                "",
                0,
                category
            )
        )

        return _inventoryList.value!![idx]
    }

    // 아이템 사용
    fun useItem(item: Inventory): Int {
        item.itemCount -= 1
        return _filterList.value!!.indexOf(item)
    }

    // 아이템 집어넣기
    fun putItem(inventoryData: Inventory): Int {
        inventoryData.itemCount++ // 아이템 개수 카운트

        return if (inventoryData.category == category.value!!) {
            _filterList.value!!.indexOf(inventoryData)
        } else {
            -1
        }
    }

    // 정원 데이터 저장
    fun saveGardenData() = viewModelScope.launch {
        repository.saveGardenData(savedGardenList.value!!)
        isEdited.value = false
    }
}