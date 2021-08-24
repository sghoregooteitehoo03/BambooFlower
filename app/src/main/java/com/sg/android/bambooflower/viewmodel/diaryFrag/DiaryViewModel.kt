package com.sg.android.bambooflower.viewmodel.diaryFrag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(private val repository: DiaryRepository) :
    ViewModel() {

    fun deleteDiary(data: Diary) = viewModelScope.launch {
        repository.deleteDiary(data)
    }
}