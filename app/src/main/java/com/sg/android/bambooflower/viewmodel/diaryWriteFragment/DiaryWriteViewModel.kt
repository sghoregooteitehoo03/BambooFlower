package com.sg.android.bambooflower.viewmodel.diaryWriteFragment

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(private val repository: DiaryWriteRepository) :
    ViewModel() {
    private val _isSaved = MutableLiveData(false)
    private val _editData = MutableLiveData<Diary?>(null)

    val contents = MutableLiveData("") // 일기 내용
    val isSaved: LiveData<Boolean> = _isSaved // 저장 여부
    val editData: LiveData<Diary?> = _editData

    // 일기 작성
    fun saveDiary(uid: String, satisfaction: Bitmap) = viewModelScope.launch {
        if (contents.value!!.isNotEmpty()) {
            val diaryData = Diary(
                id = null,
                contents = contents.value!!,
                satisfaction = satisfaction,
                timeStamp = System.currentTimeMillis(),
                uid = uid
            )

            repository.saveDiary(diaryData)
            _isSaved.value = true
        }
    }

    // 일기 수정
    fun editDiary(ordinaryData: Diary, satisfaction: Bitmap) = viewModelScope.launch {
        val editDiary = Diary(
            id = ordinaryData.id,
            contents = contents.value!!,
            satisfaction = satisfaction,
            timeStamp = ordinaryData.timeStamp,
            uid = ordinaryData.uid
        )

        repository.editDiary(editDiary)
        _editData.value = editDiary
    }
}