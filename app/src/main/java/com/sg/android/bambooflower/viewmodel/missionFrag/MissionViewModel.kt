package com.sg.android.bambooflower.viewmodel.missionFrag

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
class MissionViewModel @Inject constructor(
    private val repository: MissionRepository
) : ViewModel() {
    private val _searchPostData = MutableLiveData<Post?>(null) // 유저가 작성했던 게시글
    private val _isSearching = MutableLiveData<Boolean>(true) // 게시글 찾기

    val isLoading = MutableLiveData(true) // 로딩 창
    val isError = MutableLiveData(false) // 서버 오류

    val movePos = MutableLiveData(-1) // 리스트가 이동할 위치
    val selectedPos = MutableLiveData(-1) // 선택된 미션의 위치
    val selectedMission = MutableLiveData<Mission?>(null) // 선택된 미션 데이터
    val interstitialAd = MutableLiveData<InterstitialAd?>(null) // 광고

    val searchPostData: LiveData<Post?> = _searchPostData
    val isSearching: LiveData<Boolean> = _isSearching

    // 홈 데이터 가져오기
    fun getHomeData(isLoadMission: Boolean) =
        repository.getHomeData(isLoadMission)

    // 미션 바꾸기
    suspend fun changeMission(user: User, missions: List<Mission>) {
        isLoading.postValue(true)

        val result = repository.changeMission(user, missions)
        val updateData = result.data as Map<*, *>

        with(user) { // 유저 데이터 갱신
            myMissionTitle = updateData["myMissionTitle"] as String?
            myMissionHow = updateData["myMissionHow"] as String?
            missionDoc = updateData["missionDoc"] as String?
        }

        Log.i("ChangeMission", "성공: ${user.myMissionTitle}")
        isLoading.postValue(false)
    }

    fun searchPost(mission: Mission, user: User) = viewModelScope.launch {
        _isSearching.value = true

        _searchPostData.value = repository.searchPost(mission, user)
        _isSearching.value = false
    }

    fun checkFirst() {
        repository.checkFirst()
    }
}