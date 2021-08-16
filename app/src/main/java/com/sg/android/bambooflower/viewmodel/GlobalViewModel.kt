package com.sg.android.bambooflower.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.*

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null) // 유저 데이터
    val flower = MutableLiveData<Flower?>(null) // 꽃 데이터

    val post = MutableLiveData<Post?>() // 선택한 게시글
    val action = MutableLiveData("") // 프래그먼트 이동
    val mission = MutableLiveData<Mission?>() // 선택한 미션
    val diary = MutableLiveData<Diary?>() // 선택한 일기
    val syncData = MutableLiveData(false) // 갱신 여부

    val satisfaction = MutableLiveData<Bitmap?>(null) // 만족도
}