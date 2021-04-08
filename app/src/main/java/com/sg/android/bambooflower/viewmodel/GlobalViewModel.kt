package com.sg.android.bambooflower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.HomeData
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User

class GlobalViewModel : ViewModel() {
    val user = MutableLiveData<User?>(null)
    val post = MutableLiveData<Post?>()
    val diary = MutableLiveData<Diary?>()
    val searchPosition = MutableLiveData<Int?>(null)
}