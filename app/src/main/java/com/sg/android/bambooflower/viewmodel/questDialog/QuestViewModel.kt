package com.sg.android.bambooflower.viewmodel.questDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.sg.android.bambooflower.data.Quest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class QuestViewModel @Inject constructor(private val repository: QuestRepository) :
    ViewModel() {
    private val _buttonLoading = MutableLiveData(false) // 버튼 로딩
    private val _isAcceptComplete = MutableLiveData(-1) // 퀘스트 수락 완료

    val buttonLoading: LiveData<Boolean> = _buttonLoading
    val isAcceptComplete: LiveData<Int> = _isAcceptComplete

    val questData = MutableLiveData<Quest>() // 퀘스트 데이터
    val userQuestSize = MutableLiveData<Int>() // 유저의 수행중인 퀘스트 사이즈
    val userQuestExists = MutableLiveData<Boolean>() // 유저의 해당 퀘스트 수락 여부

    val isLoading = MutableLiveData(false) // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부

    fun getQuest(uid: String, questId: Int) = viewModelScope.launch {
        try {
            val result = repository.getUsersQuest(uid, questId)
                .data as Map<*, *>
            val complete = result["complete"]
            if (complete == null) { // 오류 확인
                throw NullPointerException()
            }

            val jsonObject = JSONObject((complete as Map<*, *>)["quest"] as Map<*, *>).toString()

            questData.value = Gson().fromJson(jsonObject, Quest::class.java)
            userQuestSize.value = complete["questSize"] as Int
            userQuestExists.value = complete["questExists"] as Boolean
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        isLoading.value = false // 로딩 끝
    }

    fun acceptQuest(uid: String, questId: Int) = viewModelScope.launch {
        _buttonLoading.value = true // 로딩 시작

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

        _buttonLoading.value = false // 로딩 끝
    }
}