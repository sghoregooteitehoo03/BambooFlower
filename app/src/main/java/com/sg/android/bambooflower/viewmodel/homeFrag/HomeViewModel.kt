package com.sg.android.bambooflower.viewmodel.homeFrag

import android.util.Log
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
class HomeViewModel @Inject constructor(private val repository: HomeRepository) :
    ViewModel() {
    private val _mainLoading = MutableLiveData(false) // 메인 로딩
    val mainLoading: LiveData<Boolean> = _mainLoading

    val isLoading = MutableLiveData(false) // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부
    val updateFlower = MutableLiveData<Flower?>(null) // 수정된 꽃 데이터

    fun getHomeData() = repository.getHomeData()

    // 꽃 -> 씨앗으로 바꿈
    fun giveUpFlower(user: User) = viewModelScope.launch {
        _mainLoading.value = true // 로딩 시작

        try {
            val result = repository.giveUpFlower(user.uid)
                .data as MutableMap<*, *>

            if (result["flower"] != null) { // 오류가 발생하지 않았을 때
                val jsonObject = JSONObject(result["flower"] as Map<*, *>).toString()
                val flowerData = Gson().fromJson(jsonObject, Flower::class.java)

                Log.i("Check", "result: $flowerData")

                user.flowerId = flowerData.id // 유저의 꽃 아이디를 수정함
                user.progress = 0 // 유저의 진행도를 수정함
                updateFlower.value = flowerData
            } else {
                isError.value = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isError.value = true
        }

        _mainLoading.value = false // 로딩 끝
    }
}