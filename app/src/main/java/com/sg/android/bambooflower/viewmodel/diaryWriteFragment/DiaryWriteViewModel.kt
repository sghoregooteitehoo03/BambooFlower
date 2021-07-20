package com.sg.android.bambooflower.viewmodel.diaryWriteFragment

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Weather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(private val repository: DiaryWriteRepository) :
    ViewModel() {
    private val _isSaved = MutableLiveData(false) // 저장 여부
    private val _editData = MutableLiveData<Diary?>(null) // 수정 여부

    val contents = MutableLiveData("") // 일기 내용
    val weather = MutableLiveData<Weather>() // 날씨 데이터
    val pos = MutableLiveData(0) // 페이저 위치
    val isSaved: LiveData<Boolean> = _isSaved
    val editData: LiveData<Diary?> = _editData

    // 일기 작성
    fun saveDiary(uid: String, resources: Resources) = viewModelScope.launch {
        val diaryData = Diary(
            id = null,
            contents = contents.value!!,
            weatherImage = BitmapFactory.decodeResource(
                resources,
                weather.value!!.weatherImage
            ),
            weather = weather.value!!.weather,
            timeStamp = System.currentTimeMillis(),
            uid = uid
        )

        repository.saveDiary(diaryData)
        _isSaved.value = true
    }

    // 일기 수정
    fun editDiary(ordinaryData: Diary, resources: Resources) = viewModelScope.launch {
        if (weather.value != null) {
            val editDiary = Diary(
                id = ordinaryData.id,
                contents = contents.value!!,
                weatherImage = BitmapFactory.decodeResource(
                    resources,
                    weather.value!!.weatherImage
                ),
                weather = weather.value!!.weather,
                timeStamp = ordinaryData.timeStamp,
                uid = ordinaryData.uid
            )

            repository.editDiary(editDiary)
            _editData.value = editDiary
        }
    }
}