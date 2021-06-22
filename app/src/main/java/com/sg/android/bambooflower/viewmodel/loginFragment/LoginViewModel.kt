package com.sg.android.bambooflower.viewmodel.loginFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.sg.android.bambooflower.other.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {
    private val _loginWay = MutableLiveData(0)
    private val _isSuccessLogin = MutableLiveData(false)

    val loginWay: LiveData<Int> = _loginWay
    val isSuccessLogin: LiveData<Boolean> = _isSuccessLogin
    val isError = MutableLiveData(false)

    fun loginEmail() {
        _loginWay.value = Contents.LOGIN_WITH_EMAIL
    }

    fun loginGoogle() {
        _loginWay.value = Contents.LOGIN_WITH_GOOGLE
    }

    fun loginFacebook() {
        _loginWay.value = Contents.LOGIN_WITH_FACEBOOK
    }

    fun login(credential: AuthCredential) = viewModelScope.launch {
        try {
            repository.login(credential)
            _isSuccessLogin.value = true
        } catch (e: Exception) {
            isError.value = true
        }
    }

    // 서버 점검 확인
    fun checkServer() =
        repository.checkServer()

    fun getUserData() =
        repository.getUserData()

    fun clear() {
        _loginWay.value = 0
        _isSuccessLogin.value = false
        isError.value = false
    }

    fun isLogin() =
        repository.isLogin()

    fun signOut(context: Context) = viewModelScope.launch {
        repository.signOut(context)
    }
}