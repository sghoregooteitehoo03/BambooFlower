package com.sg.android.bambooflower.viewmodel.questDialog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestViewModel @Inject constructor(private val repository: QuestRepository) :
    ViewModel() {
    private val _otherUserImageList = MutableLiveData<List<Uri>?>(null)
    private val _isLoading = MutableLiveData(true)
    private val _isEmpty = MutableLiveData(false)

    val isLoading: LiveData<Boolean> = _isLoading
    val otherUserImageList: LiveData<List<Uri>?> = _otherUserImageList
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun getOtherUserImages(missionDoc: String) = viewModelScope.launch {
        _otherUserImageList.value = repository.getOtherUserImages(missionDoc)

        _isEmpty.value = _otherUserImageList.value?.isEmpty()
        _isLoading.value = false
    }
}