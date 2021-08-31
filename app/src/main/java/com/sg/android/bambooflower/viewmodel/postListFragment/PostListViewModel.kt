package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.sg.android.bambooflower.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(
    private val repository: PostListRepository
) : ViewModel() {
    // 게시글 리스트
    private val _postList = repository.getPostList()
        .cachedIn(viewModelScope)
        .asLiveData()
        .let { it as MutableLiveData<PagingData<Post>> }
    private val _mainLoading = MutableLiveData(false) // 메인 로딩

    val postList: LiveData<PagingData<Post>> = _postList
    val mainLoading: LiveData<Boolean> = _mainLoading

    val isLoading = MutableLiveData<Boolean>() // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부
    val isDeleted = MutableLiveData(false) // 삭제 여부

    // 응원하기
    fun pressedCheer(uid: String, postData: Post) = viewModelScope.launch {
        postData.isCheer = true
        postData.cheerCount++

        try {
            val result = repository.pressedCheer(uid, postData.id, postData.cheerCount)
                .data as Map<*, *>

            if (result["complete"] == null) {
                throw NullPointerException()
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }
    }

    // 게시글 삭제
    fun deletePost(postData: Post) = viewModelScope.launch {
        _mainLoading.value = true // 로딩 시작

        try {
            val result = repository.deletePost(postData)
                .data as Map<*, *>

            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            _postList.value!!
                .filter { postData.id != it.id }
                .let { _postList.value = it }
            isDeleted.value = true // 삭제 완료
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _mainLoading.value = false // 로딩 끝
    }
}