package com.sg.android.bambooflower.viewmodel.addDiaryFrag

import android.content.Context
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
    private val _editData = MutableLiveData<Diary?>(null) // 수정 여부

    val contents = MutableLiveData("") // 일기 내용
    val isSaved: LiveData<Boolean> = _isSaved
    val editData: LiveData<Diary?> = _editData

    // 일기 작성
    fun saveDiary(user: User, flower: Flower) = viewModelScope.launch {
        val diaryData = Diary(
            id = null,
            contents = contents.value!!,
            user.progress,
            System.currentTimeMillis(),
            flower.image,
            flower.id,
            user.uid
        )

        repository.saveDiary(diaryData)
        _isSaved.value = true
    }

    // 일기 수정
    fun editDiary(ordinaryData: Diary, context: Context) = viewModelScope.launch {
//        if (weather.value != null) {
//            val editDiary = Diary(
//                id = ordinaryData.id,
//                contents = contents.value!!,
//                weatherImage = getBitmap(context),
//                weather = weather.value!!.weather,
//                timeStamp = ordinaryData.timeStamp,
//                uid = ordinaryData.uid
//            )
//
//            repository.editDiary(editDiary)
//            _editData.value = editDiary
//        }
    }
}