package com.sg.android.bambooflower.viewmodel.settingFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.other.Contents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: SettingRepository) :
    ViewModel() {
    private val _questSwitch = MutableLiveData<Boolean>() // 퀘스트 보상 알림 여부
    private val _diarySwitch = MutableLiveData<Boolean>() // 일기 알림 여부

    val questSwitch: LiveData<Boolean> = _questSwitch
    val diarySwitch: LiveData<Boolean> = _diarySwitch

    // 유저가 세팅했던 값을 읽어와 적용
    fun setUserSwitches(uid: String) {
        with(repository.getSettingPref()) {
            _questSwitch.value =
                getBoolean("${Contents.PREF_KEY_QUEST_ALARM}-${uid}", false)
            _diarySwitch.value =
                getBoolean("${Contents.PREF_KEY_DIARY_ALARM}-${uid}", false)
        }
    }

    fun setQuestSwitch(uid: String) {
        val isSwitched = !_questSwitch.value!!

        _questSwitch.value = isSwitched
        repository.setSettingPref(isSwitched, "${Contents.PREF_KEY_QUEST_ALARM}-${uid}")
    }

    fun setDiarySwitch(uid: String) {
        val isSwitched = !_diarySwitch.value!!

        _diarySwitch.value = isSwitched
        repository.setSettingPref(isSwitched, "${Contents.PREF_KEY_DIARY_ALARM}-${uid}")
    }

    fun signOut(context: Context) = viewModelScope.launch {
        repository.signOut(context)
    }
}