package com.sg.android.bambooflower.viewmodel.missionListFrag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MissionListViewModel @Inject constructor(
    private val repository: MissionListRepository
) : ViewModel() {

    val myMission = MutableLiveData<Mission>(null)
    val isLoading = MutableLiveData(true) // 로딩 창
    val isError = MutableLiveData(false) // 서버 오류

    val interstitialAd = MutableLiveData<InterstitialAd?>(null) // 광고

    // 홈 데이터 가져오기
    fun getHomeData(isLoadMission: Boolean) =
        repository.getHomeData(isLoadMission)

    // 미션 바꾸기
    suspend fun changeMission(user: User, missions: List<Mission>) {
        isLoading.postValue(true)

        val result = repository.changeMission(user, missions)
        val updateData = result.data as Map<*, *>

//        with(user) { // 유저 데이터 갱신
//            myMissionTitle = updateData["myMissionTitle"] as String?
//            myMissionHow = updateData["myMissionHow"] as String?
//            missionDoc = updateData["missionDoc"] as String?
//        }
//
//        Log.i("ChangeMission", "성공: ${user.myMissionTitle}")
        isLoading.postValue(false)
    }

    fun checkFirst() {
        repository.checkFirst()
    }
}