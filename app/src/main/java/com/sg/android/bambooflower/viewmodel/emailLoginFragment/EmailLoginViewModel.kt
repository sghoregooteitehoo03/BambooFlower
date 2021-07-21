package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val repository: EmailLoginRepository
) : ViewModel() {
    private var _isLoading = MutableLiveData(false) // 로딩 여부
    private val _isLoginSuccess = MutableLiveData(false) // 로그인 성공 여부
    private val _hideKeyboard = MutableLiveData(false) // 키보드 숨기기
    private val _errorEmailMsg = MutableLiveData("") // 이메일 에러 메시지
    private val _errorPasswordMsg = MutableLiveData("") // 비밀번호 에러 메시지
    val isLoading: LiveData<Boolean> = _isLoading
    val isLoginSuccess: LiveData<Boolean> = _isLoginSuccess
    val hideKeyboard: LiveData<Boolean> = _hideKeyboard

    val email = MutableLiveData("") // 이메일
    val password = MutableLiveData("") // 비밀번호
    val errorEmailMsg: LiveData<String> = _errorEmailMsg
    val errorPasswordMsg: LiveData<String> = _errorPasswordMsg

    fun login() = viewModelScope.launch { // 로그인
        val credential = EmailAuthProvider.getCredential(email.value!!, password.value!!)
        _errorEmailMsg.value = ""
        _errorPasswordMsg.value = ""

        _hideKeyboard.value = true // 키보드 숨기기
        _isLoading.value = true // 로딩 시작

        try {
            repository.login(credential)
            _isLoginSuccess.value = true // 로그인 성공
        } catch (e: Exception) {
            when ((e as FirebaseAuthException).errorCode) {
                "ERROR_USER_NOT_FOUND" -> { // 계정이 존재하지 않을 때
                    _errorEmailMsg.value = ErrorMessage.NOT_EXIST_ACCOUNT
                }
                "ERROR_WRONG_PASSWORD" -> { // 비밀번호가 잘못 되었을 때
                    _errorPasswordMsg.value = ErrorMessage.WRONG_PASSWORD
                }
                else -> {
                    _errorEmailMsg.value = ErrorMessage.ERROR_LOGIN
                }
            }
        }

        _hideKeyboard.value = false
        _isLoading.value = false // 로딩 끝
    }

    fun getUserData() =
        repository.getUserData()

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun clear() {
        _isLoginSuccess.value = false
    }
}