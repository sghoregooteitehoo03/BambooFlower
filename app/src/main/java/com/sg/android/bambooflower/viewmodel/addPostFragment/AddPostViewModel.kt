package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.User
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

    val title = MutableLiveData("") // 제목
    val content = MutableLiveData("") // 내용

    // 게시글 작성
    fun addPost(user: User, images: List<Uri>, contentResolver: ContentResolver) =
        viewModelScope.launch {
            _isLoading.value = true

            if (content.value!!.isNotEmpty() && images.isNotEmpty()) {
                try {
                    val result =
                        repository.addPost(
                            title.value!!,
                            content.value!!,
                            images,
                            user,
                            contentResolver
                        )
                    val updateData = result?.data as Map<*, *>
                    with(user) {
                        achieveState = updateData["achieveState"] as String?
                    }

                    _isSuccess.value = true
                } catch (e: Exception) {
                    errorMsg.value = "게시글 작성 중 오류가 발생하였습니다."
                }
            } else {
                errorMsg.value = "인증사진과 내용을 입력해주세요."
            }

            _isLoading.value = false
        }
}