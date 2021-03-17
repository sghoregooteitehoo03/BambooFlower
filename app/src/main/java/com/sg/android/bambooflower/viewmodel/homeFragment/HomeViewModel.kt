package com.sg.android.bambooflower.viewmodel.homeFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {
    fun getHomeData() =
        repository.getHomeData()

    fun successMission(user: User) {
        repository.successMission()
            .addOnSuccessListener {
                val updateData = it.data as Map<*, *>
                with(user) {
                    user.achievedCount = updateData["achievedCount"] as Int?
                    user.isAchieved = updateData["isAchieved"] as Boolean?
                }

                Log.i("Check", "성공: $user")
            }
    }
}