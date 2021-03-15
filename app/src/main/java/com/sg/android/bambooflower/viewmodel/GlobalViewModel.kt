package com.sg.android.bambooflower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.User

class GlobalViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()

    val user: LiveData<User> = _user

    fun setUser(data: User) {
        _user.value = data
    }
}