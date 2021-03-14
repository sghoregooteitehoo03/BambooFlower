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
    private val _mission = MutableLiveData("")

    val mission: LiveData<String> = _mission

    fun getHomeData() {
        repository.getHomeData()
            .addOnSuccessListener {
                val jsonObject = JSONObject(it.data as MutableMap<Any?, Any?>).toString()
                val mission = Gson().fromJson(jsonObject, User::class.java)
            }
    }
}