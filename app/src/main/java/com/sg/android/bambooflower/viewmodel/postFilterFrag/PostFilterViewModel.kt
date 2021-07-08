package com.sg.android.bambooflower.viewmodel.postFilterFrag

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
class PostFilterViewModel @Inject constructor(private val repository: PostFilterRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(true) // 로딩 여부
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
}