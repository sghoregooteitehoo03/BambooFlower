package com.sg.android.bambooflower.viewmodel.homeFrag

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(false) // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부

    fun getHomeData() = repository.getHomeData()
}