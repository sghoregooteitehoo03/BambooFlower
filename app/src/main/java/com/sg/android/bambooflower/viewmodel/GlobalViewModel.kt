package com.sg.android.bambooflower.viewmodel

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null) // 유저 데이터
    val missionList = MutableLiveData<List<Mission>?>(null) // 미션 리스트

    val post = MutableLiveData<Post?>() // 선택한 게시글
    val action = MutableLiveData("") // 프래그먼트 이동
    val mission = MutableLiveData<Mission?>() // 선택한 미션
    val diary = MutableLiveData<Diary?>() // 선택한 일기
    val syncData = MutableLiveData(false) // 갱신 여부

    val searchValue = MutableLiveData("") // 찾을 데이터
    val satisfaction = MutableLiveData<Bitmap?>(null) // 만족도
    val userImage = MutableLiveData<String?>(null) // 유저 이미지

    fun setSatisfaction(satisfactionRes: Int, resources: Resources) {
        satisfaction.value = BitmapFactory.decodeResource(resources, satisfactionRes)
    }
}