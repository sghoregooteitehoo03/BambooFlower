package com.sg.android.bambooflower.viewmodel.resetPasswordFrag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: ResetPasswordRepository
) : ViewModel() {
    private val _errorEmailMsg = MutableLiveData("") // 오류메시지
    private val _hideKeyboard = MutableLiveData(false)

    val email = MutableLiveData("")
    val isError = MutableLiveData(false) // 오류 여부
    val errorEmailMsg: LiveData<String> = _errorEmailMsg
    val hideKeyboard: LiveData<Boolean> = _hideKeyboard

    fun sendEmail() = viewModelScope.launch {
        try {
            _errorEmailMsg.value = ""
            _hideKeyboard.value = true // 키보드 숨기기

            repository.sendEmail(email.value!!)
            _errorEmailMsg.value = ErrorMessage.SUCCESS_SEND_EMAIL
        } catch (e: Exception) {
            when ((e as FirebaseAuthException).errorCode) {
                // 이메일 형식이 아닐 때
                "ERROR_INVALID_EMAIL" -> _errorEmailMsg.value = ErrorMessage.NOT_EMAIL_TYPE
                "ERROR_USER_NOT_FOUND" -> _errorEmailMsg.value = ErrorMessage.USE_JOIN_ACCOUNT
                else -> isError.value = true
            }
        }
    }
}