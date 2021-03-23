package com.sg.android.bambooflower.viewmodel.homeFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    private val _mission = MutableLiveData<String>()
    private val _posts = MutableLiveData<List<Post>>()

    val mission: LiveData<String> = _mission
    val posts: LiveData<List<Post>> = _posts
    val diaries = repository.getAllDiaries()
        .flow
        .cachedIn(viewModelScope)

    fun setMission(missionData: String) {
        _mission.value = missionData
    }

    fun setPosts(postsData: List<Post>) {
        _posts.value = postsData
    }

    fun getHomeData() =
        repository.getHomeData()

    fun successMission(user: User) {
        repository.successMission()
            .addOnSuccessListener {
                val updateData = it.data as Map<*, *>
                with(user) {
                    achievedCount = updateData["achievedCount"] as Int?
                    isAchieved = updateData["isAchieved"] as Boolean?
                }

                Log.i("SuccessMission", "성공: $user")
            }
    }

    fun changeMission(user: User) {
        repository.changeMission()
            .addOnSuccessListener {
                val updateData = it.data as Map<*, *>
                with(user) {
                    myMission = updateData["myMission"] as String?
                    missionDoc = updateData["missionDoc"] as String?
                }

                _mission.value = user.myMission ?: ""
                Log.i("ChangeMission", "성공: $user")
            }
    }
}