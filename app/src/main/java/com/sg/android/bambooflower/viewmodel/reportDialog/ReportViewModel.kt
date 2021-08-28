package com.sg.android.bambooflower.viewmodel.reportDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    private val _isReported = MutableLiveData(false) // 신고 여부
    private val _isLoading = MutableLiveData(false) // 로딩 여부
    private val _errorMsg = MutableLiveData("") // 에러메시지
    val isReported: LiveData<Boolean> = _isReported
    val isLoading: LiveData<Boolean> = _isLoading
    val errorMsg: LiveData<String> = _errorMsg

    val pos = MutableLiveData(-1) // 리폿 사유 위치

    // 게시글 신고
    fun reportPost(
        uid: String,
        postId: Int,
        reportReason: String
    ) = viewModelScope.launch {
        _isLoading.value = true // 로딩 시작

        try {
            val result = repository.reportPost(uid, postId, reportReason)
                .data as Map<*, *>

            if (result["complete"] == null) { // 오류 확인
                throw NullPointerException()
            }

            val complete = result["complete"] as Boolean
            if (!complete) { // 이미 신고했던 게시글일 경우
                _errorMsg.value = ErrorMessage.ALREADY_REPORT_POST
            } else {
                _isReported.value = true // 작업의 끝을 알림
            }
        } catch (e: Exception) {
            _errorMsg.value = ErrorMessage.CONNECT_ERROR
        }

        _isLoading.value = true // 로딩 끝
    }
}