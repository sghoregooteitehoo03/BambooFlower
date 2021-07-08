package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.ui.fragment.PostFilterFragment

class PostListViewModel : ViewModel() {
    val isFiltering = MutableLiveData(false)
    val fragList = MutableLiveData<List<Fragment>>(listOf(
        PostFilterFragment(false),
        PostFilterFragment(true)
    ))
}