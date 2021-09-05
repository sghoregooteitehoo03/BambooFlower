package com.sg.android.bambooflower.viewmodel.collectionDetailFrag

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
class CollectionDetailViewModel @Inject constructor(
    private val repository: CollectionDetailRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _inventoryList = MutableLiveData<List<Inventory>>() // 인벤토리 리스트

    val isLoading: LiveData<Boolean> = _isLoading
    val inventoryList: LiveData<List<Inventory>> = _inventoryList

    val isError = MutableLiveData(false) // 오류 여부

    // 인벤토리 정보 가져오기
    fun getInventoryData(uid: String) = viewModelScope.launch {
        try {
            val result = repository.getInventoryData(uid)
                .data as Map<*, *>
            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            // 리스트에 저장함
            _inventoryList.value = (result["list"] as List<*>).map { inventory ->
                val jsonObject = JSONObject(inventory as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Inventory::class.java)
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _isLoading.value = false // 로딩 끝
    }
}