package com.sg.android.bambooflower.viewmodel.addDiaryFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddDiaryViewModel @Inject constructor(private val repository: AddDiaryRepository) :
    ViewModel() {
    private val _isSaved = MutableLiveData(false) // 저장 여부
    private val _isEdited = MutableLiveData(false) // 수정 여부

    val contents = MutableLiveData("") // 일기 내용
    val isSaved: LiveData<Boolean> = _isSaved
    val editData: LiveData<Boolean> = _isEdited

    // 일기 작성
    fun saveDiary(user: User, flower: Flower) = viewModelScope.launch {
        val diaryData = Diary(
            id = null,
            contents = contents.value!!,
            progress = user.progress,
            timeStamp = System.currentTimeMillis(),
            flowerImage = flower.image,
            flowerId = flower.id,
            userId = user.uid
        )

        repository.saveDiary(diaryData)
        _isSaved.value = true
    }

    // 일기 수정
    fun editDiary(diaryData: Diary) = viewModelScope.launch {
        diaryData.contents = contents.value!!

        repository.editDiary(diaryData)
        _isEdited.value = true
    }
}