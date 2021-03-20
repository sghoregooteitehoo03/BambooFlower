package com.sg.android.bambooflower.viewmodel.addPostFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(private val repository: AddPostRepository) :
    ViewModel() {
    private val _isSuccess = MutableLiveData<Boolean>(null)

    val isSuccess: LiveData<Boolean> = _isSuccess
    val title = MutableLiveData("")
    val content = MutableLiveData("")

    fun addPost(user: User) {
        if (title.value!!.isNotEmpty() && content.value!!.isNotEmpty()) {
            repository.addPost(title.value!!, content.value!!, user)
                .addOnSuccessListener {
                    _isSuccess.value = true
                }
        } else {
            _isSuccess.value = false
        }
    }
}