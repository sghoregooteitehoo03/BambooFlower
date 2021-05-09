package com.sg.android.bambooflower.viewmodel.myMissionFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyMissionViewModel @Inject constructor(private val repository: MyMissionRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(true) // 로딩여부
    val size = MutableLiveData(-1)

    // 수행한 미션 가져옴
    fun getMyMission(uid: String) =
        repository.getMyMission(uid)
            .flow
            .cachedIn(viewModelScope)
}