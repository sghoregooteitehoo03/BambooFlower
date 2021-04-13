package com.sg.android.bambooflower.viewmodel.addPostFragment

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
    private val _isSuccess = MutableLiveData<Boolean>(null) // 성공 여부
    private val _isLoading = MutableLiveData<Boolean>(false) // 로딩
    private val _buttonAction = MutableLiveData("") // 버튼 액션

    val isSuccess: LiveData<Boolean> = _isSuccess
    val buttonAction: LiveData<String> = _buttonAction
    val isLoading: LiveData<Boolean> = _isLoading

    val title = MutableLiveData("") // 제목
    val content = MutableLiveData("") // 내용

    // 게시글 작성
    fun addPost(user: User, images: List<Uri>) = viewModelScope.launch {
        _isLoading.value = true

        if (content.value!!.isNotEmpty() && images.isNotEmpty()) {
            repository.addPost(title.value!!, content.value!!, images, user)
                .addOnSuccessListener {
                    _isSuccess.value = true
                }
        } else {
            _isSuccess.value = false
        }

        _isLoading.value = false
    }

    fun setButtonAction(action: String) {
        _buttonAction.value = action
    }
}