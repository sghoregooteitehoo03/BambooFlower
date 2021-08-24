package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(private val repository: AddPostRepository) :
    ViewModel() {
    private val _isSuccess = MutableLiveData(false) // 성공 여부
    private val _isLoading = MutableLiveData(false) // 로딩
    val isSuccess: LiveData<Boolean> = _isSuccess
    val isLoading: LiveData<Boolean> = _isLoading

    val errorMsg = MutableLiveData("") // 오류 메시지
    val image = MutableLiveData<Uri?>(null) // 이미지
    val content = MutableLiveData("") // 내용

    // 게시글 작성
    fun addPost(uid: String, usersQuest: UsersQuest, contentResolver: ContentResolver) =
        viewModelScope.launch {
            _isLoading.value = true // 로딩 시작

            try {
                val result = repository.addPost(
                    content.value!!,
                    image.value!!,
                    uid,
                    usersQuest.quest,
                    contentResolver
                ).data as Map<*, *>

                if (result["complete"] == null) { // 오류 확인
                    throw NullPointerException()
                }

                usersQuest.state = UsersQuest.STATE_LOADING
                _isSuccess.value = true
            } catch (e: Exception) {
                errorMsg.value = ErrorMessage.ERROR_ADD_POST
            }

            _isLoading.value = false // 로딩 끝
        }
}