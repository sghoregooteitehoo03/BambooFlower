package com.sg.android.bambooflower.viewmodel.profileFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    val isLoading = MutableLiveData(false) // 로딩 여부
    val size = MutableLiveData(-1)

    fun getMyPostList(uid: String) =
        repository.getMyPostList(uid)
            .cachedIn(viewModelScope)

    // 프로필 변경
//    suspend fun changeProfileImage(user: User, imageUri: Uri) {
//        isLoading.postValue(true)
//        repository.changeProfileImage(user, imageUri)
//
//        isLoading.postValue(false)
//    }
}