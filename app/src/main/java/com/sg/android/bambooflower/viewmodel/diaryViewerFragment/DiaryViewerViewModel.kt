package com.sg.android.bambooflower.viewmodel.diaryViewerFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewerViewModel @Inject constructor(private val repository: DiaryViewerRepository) :
    ViewModel() {

    fun deleteDiary(data: Diary) = viewModelScope.launch {
        repository.deleteDiary(data)
    }
}