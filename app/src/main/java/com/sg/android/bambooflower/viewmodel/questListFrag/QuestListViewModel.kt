package com.sg.android.bambooflower.viewmodel.questListFrag

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.sg.android.bambooflower.data.UsersQuest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val repository: QuestListRepository
) : ViewModel() {
    // 모든 퀘스트 목록
    val questList = repository.getQuestList()
        .cachedIn(viewModelScope)
    val usersQuestList = MutableLiveData<List<UsersQuest>>(null) // 유저가 수행중인 퀘스트
    val isLoading = MutableLiveData(true) // 로딩 여부
    val isError = MutableLiveData(false) // 오류 여부

    // 유저가 수행중인 퀘스트를 가져옴
    fun getUsersQuest(uid: String) = viewModelScope.launch {
        try {
            val result = repository.getUsersQuest(uid).data as Map<*, *>

            if (result["quests"] == null) { // 오류 확인
                throw NullPointerException()
            }

            usersQuestList.value = (result["quests"] as List<*>).map { quest ->
                val jsonObject = JSONObject(quest as Map<*, *>).toString()
                Gson().fromJson(jsonObject, UsersQuest::class.java)
            }
        } catch (e: Exception) {
            isError.value = true
            e.printStackTrace()
        }

        isLoading.value = false // 로딩 끝
    }
}