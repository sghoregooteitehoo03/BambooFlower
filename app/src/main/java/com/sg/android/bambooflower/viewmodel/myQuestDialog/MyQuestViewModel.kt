package com.sg.android.bambooflower.viewmodel.myQuestDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyQuestViewModel @Inject constructor(
    private val repository: MyQuestRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData(false) // 버튼 로딩
    private val _isGiveUpComplete = MutableLiveData(false) // 퀘스트 포기 완료
    val isLoading: LiveData<Boolean> = _isLoading
    val isGiveUpComplete: LiveData<Boolean> = _isGiveUpComplete

    val isError = MutableLiveData(false) // 오류 여부

    fun giveUpQuest(usersQuestId: Int) = viewModelScope.launch {
        _isLoading.value = true // 로딩 시작

        try {
            val result = repository.giveUpQuest(usersQuestId).data as Map<*, *>

            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            _isGiveUpComplete.value = result["complete"] as Boolean
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _isLoading.value = false// 로딩 끝
    }
}