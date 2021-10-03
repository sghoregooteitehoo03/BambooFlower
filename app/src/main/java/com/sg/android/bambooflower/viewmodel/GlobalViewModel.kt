package com.sg.android.bambooflower.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.*

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null) // 유저 데이터
    val flower = MutableLiveData<Flower?>(null) // 꽃 데이터
    val isSyncProfile = MutableLiveData(false) // 프로필 갱신

    val usersQuest = MutableLiveData<UsersQuest?>(null) // 유저의 퀘스트
    val usersQuestList = MutableLiveData<MutableList<UsersQuest>?>(null) // 유저의 퀘스트 목록
    val post = MutableLiveData<Post?>() // 선택한 게시글
    val diary = MutableLiveData<Diary?>() // 선택한 일기
    val isDeleteQuest = MutableLiveData<Boolean?>(null)
}