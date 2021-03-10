package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val repository: EmailLoginRepository
) : ViewModel() {
    private val _errorMsg = MutableLiveData("")

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val errorMsg: LiveData<String> = _errorMsg

    fun login() {
        if (email.value!!.isNotEmpty() && password.value!!.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(email.value!!, password.value!!)

            repository.login(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _errorMsg.value = ErrorMessage.SUCCESS
                    } else {
                        when ((task.exception as FirebaseAuthException).errorCode) {
                            "ERROR_USER_NOT_FOUND" -> { // 계정이 존재하지 않을 때
                                _errorMsg.value = ErrorMessage.NOT_EXIST_ACCOUNT
                            }
                            "ERROR_INVALID_EMAIL" -> { // 이메일 형식이 아닐 때
                                _errorMsg.value = ErrorMessage.NOT_EMAIL_TYPE
                            }
                            "ERROR_WRONG_PASSWORD" -> { // 비밀번호가 잘못 되었을 때
                                _errorMsg.value = ErrorMessage.WRONG_PASSWORD
                            }
                            else -> {
                                _errorMsg.value = ErrorMessage.ERROR_SIGN_UP
                            }
                        }
                    }
                }
        } else { // 이메일 및 비밀번호를 입력하지 않았을 때
            _errorMsg.value = ErrorMessage.EMAIL_PASS_EMPTY
        }
    }
}