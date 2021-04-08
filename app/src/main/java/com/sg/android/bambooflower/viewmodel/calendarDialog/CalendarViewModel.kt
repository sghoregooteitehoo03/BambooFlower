package com.sg.android.bambooflower.viewmodel.calendarDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(private val repository: CalendarRepository) :
    ViewModel() {
    val dayOfTime = MutableLiveData(System.currentTimeMillis())
    val position = MutableLiveData<Int?>(null)

    fun searchDiaryPosition(uid: String?) = viewModelScope.launch {
        position.value = repository.searchDiaryPosition(uid, dayOfTime.value!!)
    }
}