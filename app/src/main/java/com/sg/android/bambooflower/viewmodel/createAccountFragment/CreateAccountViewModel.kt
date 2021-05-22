package com.sg.android.bambooflower.viewmodel.createAccountFragment

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
class CreateAccountViewModel @Inject constructor(
    private val repository: CreateAccountRepository
) : ViewModel() {
    private val _errorMsg = MutableLiveData("")
    private val _isLoading = MutableLiveData(false)

    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val name = MutableLiveData("")
    val firstCheck = MutableLiveData(false)
    val secondCheck = MutableLiveData(false)
    val isError = MutableLiveData(false) // 서버에러

    val errorMsg: LiveData<String> = _errorMsg
    val isLoading: LiveData<Boolean> = _isLoading

    fun checkAbleData() = email.value!!.isNotEmpty() && password.value!!.isNotEmpty()
            && name.value!!.isNotEmpty()
            && firstCheck.value!!
            && secondCheck.value!!

    fun createAccount() {
        if (email.value!!.isNotEmpty() && password.value!!.isNotEmpty()
            && name.value!!.isNotEmpty()
        ) {
            _isLoading.value = true // 로딩 시작

            repository.createAccount(email.value!!, password.value!!)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        try {
                            repository.setUserData(email.value!!, password.value!!, name.value!!)

                            _errorMsg.value = ErrorMessage.SUCCESS
                            _isLoading.value = false // 로딩 끝
                        } catch (e: Exception) {
                            isError.value = true
                        }
                    }
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
                        else -> _errorMsg.value = ErrorMessage.ERROR_SIGN_UP
                    }

                    _isLoading.value = false // 로딩 끝
                }
        }
    }
}