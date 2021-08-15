package com.sg.android.bambooflower.viewmodel.createUserFragment

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(private val repository: CreateUserRepository) :
    ViewModel() {
    private val _isLoading = MutableLiveData(false) // 로딩 여부
    private val _isComplete = MutableLiveData(false) // 완료 여부

    val token = MutableLiveData("") // 토큰
    val loginWay = MutableLiveData("") // 로그인 방법

    val profileImage = MutableLiveData<Uri?>(null) // 프로필 이미지
    val email = MutableLiveData("") // 이메일
    val name = MutableLiveData("") // 닉네임
    val firstCheck = MutableLiveData(false) // 동의 여부 1
    val secondCheck = MutableLiveData(false) // 동의 여부 2

    val errorEmailMsg = MutableLiveData("") // 이메일 오류 메시지
    val errorNameMsg = MutableLiveData("") // 닉네임 오류 메시지

    val isLoading: LiveData<Boolean> = _isLoading
    val isComplete: LiveData<Boolean> = _isComplete
    val isError = MutableLiveData(false) // 서버 연결 에러

    fun setUserData(contentResolver: ContentResolver) = viewModelScope.launch {
        errorEmailMsg.value = ""
        errorNameMsg.value = ""
        _isLoading.value = true // 로딩 시작

        if (checkEmail()) { // 이메일 형식 체크
            try {
                // 유저 데이터 생성
                val result = repository.setUserData(
                    profileImage.value,
                    email.value!!,
                    name.value!!,
                    token.value!!,
                    loginWay.value!!,
                    contentResolver
                ).data as Map<*, *>

                // 동작 성공여부
                if (result["success"] as Boolean) {
                    _isComplete.value = true
                } else {
                    isError.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
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