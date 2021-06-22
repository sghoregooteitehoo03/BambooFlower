package com.sg.android.bambooflower.viewmodel.acceptListDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcceptListViewModel @Inject constructor(
    private val repository: AcceptListRepository
) : ViewModel() {

    val isLoading = MutableLiveData(true)
    val size = MutableLiveData(-1)

    // 인정한 사람들의 리스트를 가져옴
    fun getAcceptList(postPath: String) =
        repository.getAcceptList(postPath)
            .flow
            .cachedIn(viewModelScope)
}