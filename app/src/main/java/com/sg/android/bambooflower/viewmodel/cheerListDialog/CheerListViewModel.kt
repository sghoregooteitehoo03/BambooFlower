package com.sg.android.bambooflower.viewmodel.cheerListDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sg.android.bambooflower.data.Cheer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class CheerListViewModel @Inject constructor(
    private val repository: CheerListRepository
) : ViewModel() {
    val isLoading = MutableLiveData(true) // 로딩 여부
    val size = MutableLiveData(-1) // 리스트 사이즈
    val cheerList = MutableLiveData<Flow<PagingData<Cheer>>>() // 인정한 사람들의 리스트

    fun getCheerList(postId: Int) {
        cheerList.value = repository.getAcceptList(postId)
            .cachedIn(viewModelScope)
    }
}