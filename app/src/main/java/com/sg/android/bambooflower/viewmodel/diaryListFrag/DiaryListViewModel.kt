package com.sg.android.bambooflower.viewmodel.diaryListFrag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.android.gms.ads.nativead.NativeAd
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(private val repository: DiaryListRepository) :
    ViewModel() {
    val size = MutableLiveData(-1) // 일기 크기

    val diaries = repository.getAllDiaries() // 일기 리스트
        .flow
        .cachedIn(viewModelScope)
}