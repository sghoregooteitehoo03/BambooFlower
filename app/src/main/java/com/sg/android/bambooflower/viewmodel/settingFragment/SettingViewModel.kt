package com.sg.android.bambooflower.viewmodel.settingFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: SettingRepository) :
    ViewModel() {

    private val _isLoading = MutableLiveData(false) // 로딩 여부
    val isLoading: LiveData<Boolean> = _isLoading

    // 일기 모두삭제
    fun clearDiary(uid: String?) = viewModelScope.launch {
        repository.clearDiary(uid)
    }

    // 계정 탈퇴
    suspend fun deleteAccount(user: User) {
        _isLoading.postValue(true)
        repository.deleteAccount(user)

        _isLoading.postValue(false)
    }
}