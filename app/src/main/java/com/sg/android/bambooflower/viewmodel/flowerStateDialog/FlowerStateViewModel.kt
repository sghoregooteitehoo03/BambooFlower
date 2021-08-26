package com.sg.android.bambooflower.viewmodel.flowerStateDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Flower
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.UsersQuest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlowerStateViewModel @Inject constructor(
    private val repository: FlowerStateRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(true) // 로딩 여부
    private val _rewardProgress = MutableLiveData(-1) // 보상으로 얻을 유저 진행도

    val isLoading: LiveData<Boolean> = _isLoading
    val rewardProgress: LiveData<Int> = _rewardProgress

    val isError = MutableLiveData(false) // 오류 여부
    val rewardMoney = MutableLiveData(-1) // 보상으로 받을 포인트

    // 보상 받기
    fun getReward(user: User, flower: Flower, usersQuest: UsersQuest?) = viewModelScope.launch {
        setRewardAmount(user.progress, usersQuest != null)

        try {
            val result = repository.getReward(
                user.uid,
                _rewardProgress.value!! + user.progress,
                rewardMoney.value!! + user.money,
                flower,
                usersQuest?.id ?: -1
            ).data as Map<*, *>

            if (result["result"] == null) { // 오류 확인
                throw NullPointerException()
            }

            val resultMap = result["result"] as Map<*, *>

            // 업데이트 된 내용 적용
            with(user) {
                progress = resultMap["progress"] as Int
                money = resultMap["money"] as Int
                flowerId = resultMap["flowerId"] as Int
            }
            with(flower) {
                image = resultMap["flowerImage"] as String
            }

            if (usersQuest != null) {
                usersQuest.state = UsersQuest.STATE_COMPLETE
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _isLoading.value = false // 로딩 끝
    }

    // 보상 값 설정
    private fun setRewardAmount(userProgress: Int, isQuest: Boolean) {
        val progressList: List<Int>
        val moneyList: List<Int>
        val randomIndex = (0 until 5).random()

        if (isQuest) { // 퀘스트 보상 설정
            progressList = listOf(17, 18, 19, 20, 21)
            moneyList = listOf(160, 170, 180, 190, 200)
        } else { // 일기 보상 설정
            progressList = listOf(12, 13, 14, 15, 16)
            moneyList = listOf(100, 110, 120, 130, 140)
        }

        _rewardProgress.value = if (progressList[randomIndex] + userProgress > 100) {
            // 유저 진행도가 받을 진행도와 합쳤을 때 100을 넘을 시
            100 - userProgress
        } else {
            progressList[randomIndex]
        }
        rewardMoney.value = moneyList[randomIndex]
    }
}