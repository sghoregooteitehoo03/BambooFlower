package com.sg.android.bambooflower.viewmodel.postFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(private val repository: PostRepository) : ViewModel() {
    private val _isDeleted = MutableLiveData(false) // 삭제 여부
    private val _buttonAction = MutableLiveData("") // 버튼 액션

    val isDeleted: LiveData<Boolean> = _isDeleted
    val buttonAction: LiveData<String> = _buttonAction

    val isCheerUp = MutableLiveData(false) // 응원 여부
    val imagePos = MutableLiveData(1)

    fun setButtonAction(action: String) {
        _buttonAction.value = action
    }

    // 응원하기
    fun cheerUp(uid: String, postData: Post) = viewModelScope.launch {
        isCheerUp.value = repository.cheerUp(uid, postData)
    }

    // 게시글 삭제
    fun deletePost(postData: Post) = viewModelScope.launch {
        repository.deletePost(postData)
        _isDeleted.value = true
    }
}