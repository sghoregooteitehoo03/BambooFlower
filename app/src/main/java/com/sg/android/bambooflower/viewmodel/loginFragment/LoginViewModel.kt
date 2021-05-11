package com.sg.android.bambooflower.viewmodel.loginFragment

import androidx.lifecycle.*
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
        repository.login(credential)
        _isSuccessLogin.value = true
    }

    fun getUserData() =
        repository.getUserData()

    fun clear() {
        _loginWay.value = 0
        _isSuccessLogin.value = false
    }

    fun isLogin() =
        repository.isLogin()
}