package com.sg.android.bambooflower.viewmodel.shopFrag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor() : ViewModel() {
    val pagerPos = MutableLiveData(-1) // 페이저 위치
}