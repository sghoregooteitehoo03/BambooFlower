package com.sg.android.bambooflower.viewmodel.postFilterFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.ads.nativead.NativeAd
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostFilterViewModel @Inject constructor(private val repository: PostFilterRepository) :
    ViewModel() {
    private val _isMainLoading = MutableLiveData(false)
    val isMainLoading: LiveData<Boolean> = _isMainLoading

    val isLoading = MutableLiveData(true) // 로딩 여부
    val isFavorite = MutableLiveData(false) // 좋아요 여부
    val isDeleted = MutableLiveData(false) // 삭제 여부
    val isError = MutableLiveData(false) // 오류 여부

    val loadAd = MutableLiveData<NativeAd?>(null) // 로드된 광고
    val postList = MutableLiveData<Flow<PagingData<Post>>?>(null) // 게시글 리스트
    val size = MutableLiveData(-1)

    fun syncPost(uid: String = "", isFiltering: Boolean) { // 데이터 갱신
        postList.value = if (uid.isEmpty() && !isFiltering) { // 전체 게시글
            repository.getPostList()
                .flow
                .cachedIn(viewModelScope)
        } else if (uid.isEmpty() && isFiltering) { // 인증 전 게시글
            repository.getPostFilterList()
                .flow
                .cachedIn(viewModelScope)
        } else { // 내 게시글
            repository.getMyPostList(uid)
                .flow
                .cachedIn(viewModelScope)
        }
    }

    // 응원하기
    fun favorite(userData: User, postData: Post) = viewModelScope.launch {
        try {
            repository.favorite(userData, postData)
            isFavorite.value = true
        } catch (e: Exception) {
            isError.value = true
        }
    }

    // 게시글 삭제
    fun deletePost(postData: Post) = viewModelScope.launch {
        _isMainLoading.value = true // 로딩 시작

        try {
            repository.deletePost(postData) // 게시글 삭제
            isDeleted.value = true
        } catch (e: java.lang.Exception) {
            isError.value = true
        }

        _isMainLoading.value = false // 로디으 끝
    }
}