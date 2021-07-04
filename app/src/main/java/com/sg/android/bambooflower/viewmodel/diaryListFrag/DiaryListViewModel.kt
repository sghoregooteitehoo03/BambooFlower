package com.sg.android.bambooflower.viewmodel.diaryListFrag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.sg.android.bambooflower.data.DiaryDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(private val repository: DiaryListRepository) :
    ViewModel() {
    val size = MutableLiveData(-1) // 일기 크기

    val diaries = repository.getAllDiaries() // 일기 리스트
        .flow
        .map { pagingData ->
            pagingData.map { DiaryDataModel.Item(it) as DiaryDataModel }
                .insertHeaderItem(item = DiaryDataModel.Header)
        }
        .cachedIn(viewModelScope)
}