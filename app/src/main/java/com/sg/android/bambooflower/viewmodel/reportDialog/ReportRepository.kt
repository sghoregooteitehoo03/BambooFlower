package com.sg.android.bambooflower.viewmodel.reportDialog

import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.Report
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class ReportRepository @Inject constructor(private val store: FirebaseFirestore) {

    // 게시글 신고
    fun reportPost(reportData: Report) =
        store.collection(Contents.COLLECTION_REPORT)
            .document()
            .set(reportData)
}