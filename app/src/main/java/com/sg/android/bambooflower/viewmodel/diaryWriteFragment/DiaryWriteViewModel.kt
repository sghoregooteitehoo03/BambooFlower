package com.sg.android.bambooflower.viewmodel.diaryWriteFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(private val repository: DiaryWriteRepository) :
    ViewModel() {
    private val _isSaved = MutableLiveData(false)

    val contents = MutableLiveData("")
    val isSaved: LiveData<Boolean> = _isSaved

    fun saveDiary(uid: String) = viewModelScope.launch {
        if (contents.value!!.isNotEmpty()) {
            val diaryData = Diary(
                id = null,
                contents = contents.value!!,
                timeStamp = System.currentTimeMillis(),
                uid = uid
            )

            repository.saveDiary(diaryData)
            _isSaved.value = true
        }
    }
}