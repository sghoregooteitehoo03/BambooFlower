package com.sg.android.bambooflower.viewmodel.createAccountFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.other.Contents
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val repository: CreateAccountRepository
) : ViewModel() {
    private val _errorMsg = MutableLiveData("")

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val errorMsg: LiveData<String> = _errorMsg

    fun createAccount() {
        if (email.value!!.isNotEmpty() && password.value!!.isNotEmpty()) {
            repository.createAccount(email.value!!, password.value!!)
                .addOnSuccessListener {
                    _errorMsg.value = ErrorMessage.SUCCESS
                }
                .addOnFailureListener {
                    when ((it as FirebaseAuthException).errorCode) {
                        // 이메일 형식이 아닐 때
                        "ERROR_INVALID_EMAIL" -> _errorMsg.value =
                            ErrorMessage.NOT_EMAIL_TYPE
                        // 비밀번호가 약할 때
                        "ERROR_WEAK_PASSWORD" -> _errorMsg.value =
                            ErrorMessage.NOT_POWERFUL_PASSWORD
                        // 이미 계정이 존재 할 때
                        "ERROR_EMAIL_ALREADY_IN_USE" -> _errorMsg.value =
                            ErrorMessage.ALREADY_EXIST_ACCOUNT
                        else -> _errorMsg.value = ErrorMessage.ERROR_LOGIN
                    }
                }
        } else { // 이메일이나 비밀번호가 입력되있지 않을 때
            _errorMsg.value = ErrorMessage.EMAIL_PASS_EMPTY
        }
    }
}