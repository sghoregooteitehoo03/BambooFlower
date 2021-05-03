package com.sg.android.bambooflower.viewmodel.homeFragment

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertHeaderItem
import androidx.paging.map
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.DiaryDataModel
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    private val _isChanging = MutableLiveData(false) // 미션 로딩 뷰

    val posts = MutableLiveData<List<Post>>() // 게시글
    val isLoading = MutableLiveData(true) // 로딩 창
    val currentTime = MutableLiveData<Long>() // 현재 시간

    val isChanging: LiveData<Boolean> = _isChanging
    val diaries = repository.getAllDiaries() // 일기 리스트
        .flow
        .map { pagingData ->
            pagingData.map { DiaryDataModel.Item(it) as DiaryDataModel }
                .insertHeaderItem(item = DiaryDataModel.Header)
        }
        .cachedIn(viewModelScope)

    // 홈 화면에 표시할 데이터를 가져옴
    fun getHomeData() =
        repository.getHomeData()

    // 미션 완수
//    fun successMission(user: User) {
//        repository.successMission()
//            .addOnSuccessListener {
//                val updateData = it.data as Map<*, *>
//                with(user) {
//                    achievedCount = updateData["achievedCount"] as Int?
//                    achieved = updateData["isAchieved"] as Boolean?
//                }
//
//                Log.i("SuccessMission", "성공: $user")
//                isAchieved.value = true
//            }
//    }

    // 미션 변경
    suspend fun changeMission(user: User) {
        _isChanging.postValue(true)

        val result = repository.changeMission()
        val updateData = result.data as Map<*, *>
        with(user) {
            myMission = updateData["myMission"] as String?
            missionDoc = updateData["missionDoc"] as String?
        }

        Log.i("ChangeMission", "성공: ${user.myMission}")
        _isChanging.postValue(false)
    }
}