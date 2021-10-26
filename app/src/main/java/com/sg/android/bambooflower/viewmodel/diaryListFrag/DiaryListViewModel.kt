package com.sg.android.bambooflower.viewmodel.diaryListFrag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.google.android.gms.ads.nativead.NativeAd
import com.sg.android.bambooflower.data.DiaryItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(private val repository: DiaryListRepository) :
    ViewModel() {
    val nativeAd = MutableLiveData<NativeAd?>(null) // 로드 된 광고
    val size = MutableLiveData(-1) // 일기 크기

    val diaries = repository.getAllDiaries() // 일기 리스트
        .map { pagingData ->
            pagingData.map { DiaryItemModel.Item(it) as DiaryItemModel }
                .insertHeaderItem(item = DiaryItemModel.Header(nativeAd.value))
        }
        .cachedIn(viewModelScope)

    fun getPref() =
        repository.getPref()
}