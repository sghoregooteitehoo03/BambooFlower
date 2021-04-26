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
    private val _buttonAction = MutableLiveData("")
    private val _isLoading = MutableLiveData(false)

    val buttonAction: LiveData<String> = _buttonAction // 버튼 액션
    val isLoading: LiveData<Boolean> = _isLoading // 로딩 여부

    fun setButtonAction(action: String) {
        _buttonAction.value = action
    }

    // 프로필 변경
    suspend fun changeProfileImage(user: User, imageUri: Uri) = viewModelScope.launch {
        _isLoading.value = true
        repository.changeProfileImage(user, imageUri)

        _isLoading.value = false
    }

    // 로그아웃
    fun signOut() {
        repository.signOut()
    }
}