package com.sg.android.bambooflower.viewmodel.loginFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.sg.android.bambooflower.other.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun login(credential: AuthCredential) {
        repository.login(credential)
            .addOnSuccessListener {
                _isSuccessLogin.value = true
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun clear() {
        _loginWay.value = 0
        _isSuccessLogin.value = false
    }
}