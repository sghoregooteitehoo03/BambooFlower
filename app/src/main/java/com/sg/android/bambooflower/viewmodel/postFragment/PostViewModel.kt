package com.sg.android.bambooflower.viewmodel.postFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {
    private val _isDeleted = MutableLiveData(false) // 삭제 여부
    private val _isLoading = MutableLiveData(false) // 로딩

    val isDeleted: LiveData<Boolean> = _isDeleted
    val isLoading:LiveData<Boolean> = _isLoading // 로딩

    val isError = MutableLiveData(false) // 서버 에러
    val isCheerUp = MutableLiveData(false) // 응원 여부
    val imagePos = MutableLiveData(0) // 이미지 위치

    // 응원하기
    fun cheerUp(uid: String, postData: Post) = viewModelScope.launch {
        try {
            isCheerUp.value = repository.cheerUp(uid, postData)
        } catch (e: Exception) {
            isError.value = true
            _isLoading.value = false
        }
    }

    // 게시글 삭제
    fun deletePost(postData: Post) = viewModelScope.launch {
        _isLoading.value = true

        try {
            repository.deletePost(postData)

            _isDeleted.value = true
            _isLoading.value = false
        } catch (e: java.lang.Exception) {
            isError.value = true
            _isLoading.value = false
        }
    }
}