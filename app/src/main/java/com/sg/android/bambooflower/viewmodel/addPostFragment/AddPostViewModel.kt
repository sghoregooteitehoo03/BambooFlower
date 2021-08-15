package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.User
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
    val image = MutableLiveData("") // 이미지
    val content = MutableLiveData("") // 내용

    // 게시글 작성
    fun addPost(user: User, contentResolver: ContentResolver) =
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val result =
                    repository.addPost(
                        content.value!!,
                        image.value!!.toUri(),
                        user,
                        contentResolver
                    )
                val updateData = result?.data as Map<*, *>
                with(user) {
//                    achieveState = updateData["achieveState"] as String?
                }

                _isSuccess.value = true
            } catch (e: Exception) {
                errorMsg.value = ErrorMessage.ERROR_ADD_POST
            }

            _isLoading.value = false
        }
}