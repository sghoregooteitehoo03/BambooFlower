package com.sg.android.bambooflower.viewmodel.questDialog

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
    private val _isActionLoading = MutableLiveData(false) // 버튼 로딩
    private val _isGiveUpLoading = MutableLiveData(false) // 포기 버튼 로딩
    private val _isAcceptComplete = MutableLiveData(-1) // 퀘스트 수락 완료
    private val _isGiveUpComplete = MutableLiveData(false) // 퀘스트 포기 완료

    val isActionLoading: LiveData<Boolean> = _isActionLoading
    val isGiveUpLoading: LiveData<Boolean> = _isGiveUpLoading
    val isAcceptComplete: LiveData<Int> = _isAcceptComplete
    val isGiveUpComplete: LiveData<Boolean> = _isGiveUpComplete

    val isError = MutableLiveData(false) // 오류 여부

    fun acceptQuest(uid: String, questId: Int) = viewModelScope.launch {
        _isActionLoading.value = true // 로딩 시작

        try {
            val result = repository.acceptQuest(uid, questId).data as Map<*, *>

            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            _isAcceptComplete.value = result["complete"] as Int // 퀘스트 수락 완료
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        _isActionLoading.value = false // 로딩 끝
    }

    fun giveUpQuest(usersQuestId: Int) = viewModelScope.launch {
        _isGiveUpLoading.value = true // 로딩 시작

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

        _isGiveUpLoading.value = false// 로딩 끝
    }
}