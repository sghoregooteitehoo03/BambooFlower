package com.sg.android.bambooflower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null) // 유저 데이터
    val post = MutableLiveData<Post?>() // 선택한 게시글
    val diary = MutableLiveData<Diary?>() // 선택한 일기
    val searchPosition = MutableLiveData<Int?>(null) // 일기 위치

    val searchValue = MutableLiveData("") // 찾을 데이터
    val satisfaction = MutableLiveData(R.drawable.low_image) // 만족도

    fun setSatisfaction(_satisfaction: Int) {
        satisfaction.value = _satisfaction
    }
}