package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import androidx.lifecycle.*
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
    private var _isLoading = MutableLiveData(false)

    val email = MutableLiveData("")
    val password = MutableLiveData("")

    val errorMsg: LiveData<String> = _errorMsg
    val isLoading: LiveData<Boolean> = _isLoading

    fun login() {
        if (email.value!!.isNotEmpty() && password.value!!.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(email.value!!, password.value!!)
            _isLoading.value = true

            repository.login(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _errorMsg.value = ErrorMessage.SUCCESS
                    } else {
                        when ((task.exception as FirebaseAuthException).errorCode) {
                            "ERROR_USER_NOT_FOUND" -> { // 계정이 존재하지 않을 때
                                _errorMsg.value = ErrorMessage.NOT_EXIST_ACCOUNT
                            }
                            "ERROR_WRONG_PASSWORD" -> { // 비밀번호가 잘못 되었을 때
                                _errorMsg.value = ErrorMessage.WRONG_PASSWORD
                            }
                            else -> {
                                _errorMsg.value = ErrorMessage.ERROR_LOGIN
                            }
                        }

                        _isLoading.value = false
                    }
                }
        }
    }

    fun getUserData() =
        repository.getUserData()

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun clear() {
        _errorMsg.value = ""
    }
}