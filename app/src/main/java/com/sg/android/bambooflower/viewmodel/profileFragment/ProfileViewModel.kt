package com.sg.android.bambooflower.viewmodel.profileFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {
    private val _myPosts = MutableLiveData<List<Post>>(listOf())

    val myPosts: LiveData<List<Post>> = _myPosts

    fun getPreviewPost() {
        repository.getPreviewPost()
            .addOnSuccessListener {
                _myPosts.value = it.toObjects(Post::class.java)
            }
    }
}