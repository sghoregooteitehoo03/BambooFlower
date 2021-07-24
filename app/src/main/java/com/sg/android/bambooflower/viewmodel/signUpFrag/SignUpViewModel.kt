package com.sg.android.bambooflower.viewmodel.signUpFrag

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpRepository
) : ViewModel() {
    private val _isSuccessLogin = MutableLiveData(false) // 로그인 성공 여부
    private val _hideKeyBoard = MutableLiveData(false) // 키보드 숨기기
    val isSuccessLogin: LiveData<Boolean> = _isSuccessLogin
    val hideKeyBoard: LiveData<Boolean> = _hideKeyBoard

    val email = MutableLiveData("") // 이메일
    val password = MutableLiveData("") // 비밀번호
    val repassword = MutableLiveData("") // 비밀번호 재입력

    val errorEmailMsg = MutableLiveData("") // 이메일 에러메시지
    val errorPasswordMsg = MutableLiveData("") // 비밀번호 에러메시지
    val errorRepasswordMsg = MutableLiveData("") // 비밀번호 재입력 에러메시지

    val isError = MutableLiveData(false)
    val isLoading = MutableLiveData(false)

    // 구글 및 페이스북 로그인
    fun login(credential: AuthCredential) = viewModelScope.launch {
        try {
            repository.login(credential)
            _isSuccessLogin.value = true
        } catch (e: Exception) {
            isError.value = true
        }
    }

    // 회원가입
    fun signUp() = viewModelScope.launch {
        try {
            errorEmailMsg.value = ""
            errorPasswordMsg.value = ""
            errorRepasswordMsg.value = ""

            isLoading.value = true // 로딩 시작
            _hideKeyBoard.value = true // 키보드 숨기기

            if (repassword.value == password.value) {
                repository.signUp(email.value!!, password.value!!)
                _isSuccessLogin.value = true

            } else { // 비밀번호 재입력에서 다른게 입력하였을 때
                errorRepasswordMsg.value = "비밀번호를 다시 한번 확인해주세요."
            }
        } catch (e: Exception) {
            when ((e as FirebaseAuthException).errorCode) {
                // 이메일 형식이 아닐 때
                "ERROR_INVALID_EMAIL" -> errorEmailMsg.value = ErrorMessage.NOT_EMAIL_TYPE
                // 비밀번호가 약할 때
                "ERROR_WEAK_PASSWORD" -> errorPasswordMsg.value = ErrorMessage.NOT_POWERFUL_PASSWORD
                // 이미 계정이 존재 할 때
                "ERROR_EMAIL_ALREADY_IN_USE" -> errorEmailMsg.value = ErrorMessage.ALREADY_EXIST_ACCOUNT
                else -> isError.value = true
            }
        }

        _hideKeyBoard.value = false
        isLoading.value = false
    }

    // 서버 점검 확인
    fun checkServer() =
        repository.checkServer()

    fun getUserData() =
        repository.getUserData()

    fun clear() {
        _isSuccessLogin.value = false
        isError.value = false
    }

    fun isLogin() =
        repository.isLogin()

    fun signOut(context: Context) = viewModelScope.launch {
        repository.signOut(context)
    }
}