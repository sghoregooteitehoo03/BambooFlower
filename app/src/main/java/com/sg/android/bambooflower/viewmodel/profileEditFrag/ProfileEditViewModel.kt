package com.sg.android.bambooflower.viewmodel.profileEditFrag

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val repository: ProfileEditRepository
) : ViewModel() {
    private val _errorEmailMsg = MutableLiveData("") //  이메일 에러 메시지
    private val _isLoading = MutableLiveData(false) // 로딩 여부
    private val _isUpdate = MutableLiveData(false) // 업데이트 여부

    val errorEmailMsg: LiveData<String> = _errorEmailMsg
    val isLoading: LiveData<Boolean> = _isLoading
    val isUpdate: LiveData<Boolean> = _isUpdate

    val profileImage = MutableLiveData("") // 프로필 이미지
    val email = MutableLiveData("") // 이메일
    val name = MutableLiveData("") // 닉네임
    val isError = MutableLiveData(false) // 에러 여부

    fun changeProfile(user: User) = viewModelScope.launch {
        _isLoading.value = true // 로딩 시작
        _errorEmailMsg.value = ""

        // 이메일 형식이 맞는지 확인
        if (checkEmail()) {
            try {
                repository.changeProfile(
                    user,
                    profileImage.value!!,
                    email.value!!,
                    name.value!!
                )
//                user.profileImage = profileImage.value!!
                user.email = email.value!!
                user.name = name.value!!

                _isUpdate.value = true // 업데이트 성공
            } catch (e: Exception) {
                isError.value = true
            }
        } else {
            _errorEmailMsg.value = ErrorMessage.NOT_EMAIL_TYPE
        }

        _isLoading.value = false // 로딩 끝
    }

    // 이메일 형식 확인
    private fun checkEmail() =
        Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()
}