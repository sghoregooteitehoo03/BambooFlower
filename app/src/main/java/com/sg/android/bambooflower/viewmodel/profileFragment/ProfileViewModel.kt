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
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading // 로딩 여부

    // 프로필 변경
    suspend fun changeProfileImage(user: User, imageUri: Uri) {
        _isLoading.postValue(true)
        repository.changeProfileImage(user, imageUri)

        _isLoading.postValue(false)
    }

    // 로그아웃
    fun signOut() {
        repository.signOut()
    }
}