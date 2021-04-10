package com.sg.android.bambooflower.viewmodel.postSearchFragment

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostSearchViewModel @Inject constructor(private val repository: PostSearchRepository) :
    ViewModel() {
}