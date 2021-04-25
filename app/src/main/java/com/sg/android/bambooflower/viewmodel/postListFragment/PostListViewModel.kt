package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sg.android.bambooflower.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PostListViewModel @Inject constructor(private val postListRepository: PostListRepository) :
    ViewModel() {

    val isLoading = MutableLiveData(true) // 로딩 여부
    val postList = MutableLiveData<Flow<PagingData<Post>>?>(null) // 게시글 리스트

    fun syncPost(uid: String = "") { // 데이터 갱신
        postList.value = if (uid.isEmpty()) {
            postListRepository.getPostList()
                .flow
                .cachedIn(viewModelScope)
        } else {
            postListRepository.getMyPostList(uid)
                .flow
                .cachedIn(viewModelScope)
        }

        isLoading.value = false
    }
}