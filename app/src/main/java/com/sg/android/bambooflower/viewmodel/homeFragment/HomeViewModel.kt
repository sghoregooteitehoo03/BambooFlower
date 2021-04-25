package com.sg.android.bambooflower.viewmodel.homeFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _buttonAction = MutableLiveData("") // 버튼 액션
    private val _isChanging = MutableLiveData(false) // 미션 로딩 뷰

    val mission = MutableLiveData<String>() // 미션
    val posts = MutableLiveData<List<Post>>() // 게시글
    val isAchieved = MutableLiveData<Boolean>() // 미션 수행 확인
    val isLoading = MutableLiveData(true) // 로딩 창
    val buttonEnabled = MutableLiveData(true) // 버튼 활성화
    val currentTime = MutableLiveData<Long>() // 현재 시간

    val buttonAction: LiveData<String> = _buttonAction
    val isChanging: LiveData<Boolean> = _isChanging
    val diaries = repository.getAllDiaries() // 일기 리스트
        .flow
        .cachedIn(viewModelScope)

    fun setButtonAction(action: String) {
        _buttonAction.value = action
    }

    // 홈 화면에 표시할 데이터를 가져옴
    fun getHomeData() =
        repository.getHomeData()

    // 미션 완수
    fun successMission(user: User) {
        repository.successMission()
            .addOnSuccessListener {
                val updateData = it.data as Map<*, *>
                with(user) {
                    achievedCount = updateData["achievedCount"] as Int?
                    achieved = updateData["isAchieved"] as Boolean?
                }

                Log.i("SuccessMission", "성공: $user")
                isAchieved.value = true
            }
    }

    // 미션 변경
    fun changeMission(user: User) {
        _isChanging.value = true
        buttonEnabled.value = false

        repository.changeMission()
            .addOnSuccessListener {
                val updateData = it.data as Map<*, *>
                with(user) {
                    myMission = updateData["myMission"] as String?
                    missionDoc = updateData["missionDoc"] as String?
                }

                mission.value = user.myMission ?: ""
                Log.i("ChangeMission", "성공: $user")

                _isChanging.value = false
                buttonEnabled.value = true
            }
    }
}