package com.sg.android.bambooflower.viewmodel.selectFlowerDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class SelectFlowerViewModel @Inject constructor(
    private val repository: SelectFlowerRepository
) : ViewModel() {
    private val _selectLoading = MutableLiveData(false) // 선택창 로딩
    private val _isLoading = MutableLiveData(true) // 리스트 로딩 여부
    private val _flowerList = MutableLiveData<List<Flower>?>(null) // 꽃 리스트
    private val _selectedFlower = MutableLiveData<Flower?>(null) // 선택한 꽃

    val selectLoading: LiveData<Boolean> = _selectLoading
    val isLoading: LiveData<Boolean> = _isLoading
    val flowerList: LiveData<List<Flower>?> = _flowerList
    val selectedFlower: LiveData<Flower?> = _selectedFlower

    val isError = MutableLiveData(false) // 오류 여부
    val selectPos = MutableLiveData(-1) // 꽃 클릭 위치

    // 내가 가지고 있는 꽃 아이템을 가져옴
    fun getHavenFlowerData(uid: String) = viewModelScope.launch {
        try {
            val result = repository.getHavenFlowerData(uid).data as Map<*, *>

            _flowerList.value = (result["flowerList"] as List<*>).map { flower ->
                val jsonObject = JSONObject(flower as MutableMap<*, *>).toString()
                Gson().fromJson(jsonObject, Flower::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isError.value = true
        }

        _isLoading.value = false // 로딩 끝
    }

    fun selectFlower(user: User, flowerName: String) = viewModelScope.launch {
        _selectLoading.value = true // 로딩 시작

        try {
            val result = repository.selectFlower(user.uid, flowerName)
                .data as MutableMap<*, *>

            if (result["flower"] != null) { // 오류가 발생하지 않았을 때
                val jsonObject = JSONObject(result["flower"] as Map<*, *>).toString()
                val flowerData = Gson().fromJson(jsonObject, Flower::class.java)

                user.flowerId = flowerData.id // 유저의 꽃 아이디를 수정함
                _selectedFlower.value = flowerData
            } else {
                isError.value = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isError.value = true
        }

        _selectLoading.value = false // 로딩 끝
    }
}