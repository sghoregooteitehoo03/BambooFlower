package com.sg.android.bambooflower.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null) // 유저 데이터
    val post = MutableLiveData<Post?>() // 선택한 게시글
    val diary = MutableLiveData<Diary?>() // 선택한 일기
    val searchPosition = MutableLiveData<Int?>(null) // 일기 위치
    val syncData = MutableLiveData(false) // 갱신 여부

    val searchValue = MutableLiveData("") // 찾을 데이터
    val satisfaction = MutableLiveData<Bitmap?>(null) // 만족도

    fun setSatisfaction(satisfactionRes: Int, resources: Resources) {
        satisfaction.value = BitmapFactory.decodeResource(resources, satisfactionRes)
    }
}