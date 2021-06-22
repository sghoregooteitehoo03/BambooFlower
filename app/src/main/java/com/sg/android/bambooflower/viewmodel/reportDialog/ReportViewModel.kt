package com.sg.android.bambooflower.viewmodel.reportDialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.Report
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(private val repository: ReportRepository) : ViewModel() {
    private val _isReported = MutableLiveData(false) // 신고 여부
    val isReported: LiveData<Boolean> = _isReported

    val reportReason = MutableLiveData("") // 신고 사유

    // 게시글 신고
    fun reportPost(postData: Post) {
        if (reportReason.value!!.isNotEmpty()) {
            val reportData = Report(postData.postPath!!, reportReason.value!!)

            repository.reportPost(reportData).addOnSuccessListener {
                _isReported.value = true
            }
        }
    }
}