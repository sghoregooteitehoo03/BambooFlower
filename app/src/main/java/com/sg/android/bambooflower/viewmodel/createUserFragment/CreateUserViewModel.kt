package com.sg.android.bambooflower.viewmodel.createUserFragment

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Account
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(private val repository: CreateUserRepository) :
    ViewModel() {
    private val _isLoading = MutableLiveData(false) // 로딩 여부
    private val _isComplete = MutableLiveData(false) // 완료 여부

    val password = MutableLiveData("") // 비밀번호
    val token = MutableLiveData("") // 토큰
    val loginWay = MutableLiveData("") // 로그인 방법

    val profileImage = MutableLiveData("") // 프로필 이미지
    val email = MutableLiveData("") // 이메일
    val name = MutableLiveData("") // 닉네임
    val firstCheck = MutableLiveData(false) // 동의 여부 1
    val secondCheck = MutableLiveData(false) // 동의 여부 2

    val errorEmailMsg = MutableLiveData("") // 이메일 오류 메시지
    val errorNameMsg = MutableLiveData("") // 닉네임 오류 메시지

    val isLoading: LiveData<Boolean> = _isLoading
    val isComplete: LiveData<Boolean> = _isComplete
    val isError = MutableLiveData(false) // 서버 연결 에러

    fun setUserData() = viewModelScope.launch {
        errorEmailMsg.value = ""
        errorNameMsg.value = ""
        _isLoading.value = true // 로딩 시작

        if (checkEmail()) { // 이메일 형식 체크
            try {
                val account = if (loginWay.value != "Email") {
                    // 페이스북 및 구글로 계정을 만들 시
                    Account(
                        loginWay.value!!,
                        token.value!!,
                        email.value!!
                    )
                } else {
                    // 이메일로 계정을 만들 시
                    Account(
                        loginWay.value!!,
                        null,
                        email.value!!,
                        password.value!!
                    )
                }

                // 유저 데이터 생성
                repository.setUserData(
                    profileImage.value!!,
                    account,
                    name.value!!
                )
                _isComplete.value = true
            } catch (e: Exception) {
                isError.value = true
            }
        } else {
            errorEmailMsg.value = ErrorMessage.NOT_EMAIL_TYPE
        }

        _isLoading.value = false // 로딩 끝
    }

    // 이메일 형식 확인
    private fun checkEmail() =
        Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()

    fun signOut(context: Context) = viewModelScope.launch {
        repository.signOut(context)
    }
}