package com.sg.android.bambooflower.viewmodel.profileFragment

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
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(true) // 로딩 여부
    val size = MutableLiveData(-1)
    val postList = MutableLiveData<Flow<PagingData<Post>>?>(null) // 게시글 리스트

//    fun getMyPostList(uid: String) {
//        postList.value = repository.getMyPostList(uid)
//            .cachedIn(viewModelScope)
//    }
}