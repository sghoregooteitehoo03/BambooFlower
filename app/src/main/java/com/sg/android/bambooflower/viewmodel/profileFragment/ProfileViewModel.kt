package com.sg.android.bambooflower.viewmodel.profileFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _flowerList = MutableLiveData<List<Flower>>() // 꽃 리스트
    private val _postList = MutableLiveData<List<Post>>() // 게시글 리스트

    val isLoading: LiveData<Boolean> = _isLoading
    val flowerList: LiveData<List<Flower>> = _flowerList
    val postList: LiveData<List<Post>> = _postList

    val isRefresh = MutableLiveData(false) // 갱신 여부
    val isError = MutableLiveData(false) // 오류 여부
    val postSize = MutableLiveData(-1) // 인증 활동 사이즈

    // 프로필 화면에 보여줄 데이터 가져오기
    fun getProfileData(uid: String) = viewModelScope.launch {
        try {
            val result = repository.getProfileData(uid)
                .data as Map<*, *>
            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            _flowerList.value = (result["flowerList"] as List<*>).map { flower ->
                val jsonObject = JSONObject(flower as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Flower::class.java)
            }
            _postList.value = (result["postList"] as List<*>).map { post ->
                val jsonObject = JSONObject(post as Map<*, *>).toString()
                Gson().fromJson(jsonObject, Post::class.java)
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        isRefresh.value = false // 갱신 끝
        _isLoading.value = false // 로딩 끝
    }
}