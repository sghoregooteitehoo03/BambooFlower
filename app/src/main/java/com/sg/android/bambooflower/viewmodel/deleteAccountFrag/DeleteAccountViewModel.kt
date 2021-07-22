package com.sg.android.bambooflower.viewmodel.deleteAccountFrag

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthException
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val repository: DeleteAccountRepository
) : ViewModel() {
    private val _isLoading = MutableLiveData(false) // 로딩여부
    private val _isDeleted = MutableLiveData(false) // 삭제여부
    private val _errorInputMsg = MutableLiveData("") // 에러메시지
    private val _hideKeyboard = MutableLiveData(false) // 키보드 숨기기 여부

    val isLoading: LiveData<Boolean> = _isLoading
    val isDeleted: LiveData<Boolean> = _isDeleted
    val errorInputMsg: LiveData<String> = _errorInputMsg
    val hideKeyboard: LiveData<Boolean> = _hideKeyboard

    val inputData = MutableLiveData("")
    val isEnabled = MutableLiveData(false)
    val isError = MutableLiveData(false) // 에러 여부

    fun deleteAccount(user: User, context: Context) = viewModelScope.launch {
        _errorInputMsg.value = ""
        _hideKeyboard.value = true
        _isLoading.value = true // 로딩 시작

        try {
            repository.reLogin(user, inputData.value!!) // 재로그인
            repository.deleteAccount(user, context) // 계정 삭제

            _isDeleted.value = true
        } catch (e: Exception) {
            if (e is FirebaseAuthException) {
                // 로그인 시 오류가 발생할 때
                when (e.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> { // 비밀번호가 잘못 되었을 때
                        _errorInputMsg.value = ErrorMessage.WRONG_PASSWORD
                    }
                    else -> {
                        isError.value = true
                    }
                }
            } else {
                // 다른 이유로 오류가 발생할 때
                isError.value = true
            }
        }

        _hideKeyboard.value = false
        _isLoading.value = false // 로딩 끝
    }
}