package com.sg.android.bambooflower.viewmodel.createUserFragment

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.viewmodel.createAccountFragment.CreateAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(private val repository: CreateAccountRepository) :
    ViewModel() {
    private val _errorMsg = MutableLiveData("")
    private val _isLoading = MutableLiveData(false)

    val email = MutableLiveData("")
    val name = MutableLiveData("")

    val errorMsg: LiveData<String> = _errorMsg
    val isLoading: LiveData<Boolean> = _isLoading

    fun setUserData() {
        if (email.value!!.isNotEmpty() && name.value!!.isNotEmpty()) {
            _isLoading.value = true // 로딩 시작

            if (checkEmail()) { // 이메일 형식 체크
                viewModelScope.launch {
                    repository.setUserData(email.value!!, name.value!!)

                    _errorMsg.value = ErrorMessage.SUCCESS
                    _isLoading.value = false // 로딩 끝
                }
            } else {
                _errorMsg.value = ErrorMessage.NOT_EMAIL_TYPE
                _isLoading.value = false // 로딩 끝
            }
        }
    }

    // 이메일 형식 확인
    private fun checkEmail() =
        Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()
}