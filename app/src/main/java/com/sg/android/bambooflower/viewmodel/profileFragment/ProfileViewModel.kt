package com.sg.android.bambooflower.viewmodel.profileFragment

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(false) // 로딩 여부

    // 프로필 변경
    suspend fun changeProfileImage(user: User, imageUri: Uri) {
        isLoading.postValue(true)
        repository.changeProfileImage(user, imageUri)

        isLoading.postValue(false)
    }

    // 로그아웃
    fun signOut() {
        repository.signOut()
    }
}