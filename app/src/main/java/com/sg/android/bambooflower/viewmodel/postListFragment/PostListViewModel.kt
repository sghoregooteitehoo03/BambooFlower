package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.lifecycle.*
import androidx.paging.*
import com.google.android.gms.ads.nativead.NativeAd
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.PostItemModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PostListViewModel @AssistedInject constructor(
    private val repository: PostListRepository,
    @Assisted private val isFilter: Boolean
) : ViewModel() {
    // 게시글 리스트
    private val _postList = MutableLiveData<PagingData<PostItemModel>?>(null)
    private val _mainLoading = MutableLiveData(false) // 메인 로딩
    val postList: LiveData<PagingData<PostItemModel>?> = _postList
    val mainLoading: LiveData<Boolean> = _mainLoading

    val nativeAd = MutableLiveData<NativeAd?>(null) // 로드 된 광고
    val postSize = MutableLiveData(-1) // 게시글 크기
    val isRefresh = MutableLiveData(false) // 새로고침 여부
    val isLoading = MutableLiveData(true) // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부
    val isDeleted = MutableLiveData(false) // 삭제 여부

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(isFilter: Boolean): PostListViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            isFilter: Boolean = false
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(isFilter) as T
            }
        }
    }

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
                .filter { if (it is PostItemModel.Item) postData.id != it.post.id else true }
                .let { _postList.value = it }
            isDeleted.value = true // 삭제 완료
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _mainLoading.value = false // 로딩 끝
    }

    fun getPostList() = viewModelScope.launch {
        repository.getPostList(isFilter)
            .map { pagingData ->
                if (isFilter) { // 내 게시글
                    pagingData.map { PostItemModel.Item(it) as PostItemModel }
                } else { // 모든 게시글
                    pagingData.map { PostItemModel.Item(it) as PostItemModel }
                        .insertHeaderItem(item = PostItemModel.Header(nativeAd.value))
                }
            }
            .cachedIn(viewModelScope)
            .collect {
                _postList.value = it
            }
    }

    // 기존에 저장되어있던 데이터를 지움
    fun clearData() {
        _postList.value = null
        nativeAd.value = null
        isLoading.value = true
        postSize.value = -1
    }
}