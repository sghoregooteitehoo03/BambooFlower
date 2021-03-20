package com.sg.android.bambooflower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User

class GlobalViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    private val _post = MutableLiveData<Post?>()

    val user: LiveData<User> = _user
    val post: LiveData<Post?> = _post

    fun setUser(data: User) {
        _user.value = data
    }

    fun setPost(postData: Post?) {
        _post.value = postData
    }
}