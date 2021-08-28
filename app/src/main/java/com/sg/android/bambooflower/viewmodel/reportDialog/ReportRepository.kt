package com.sg.android.bambooflower.viewmodel.reportDialog

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class ReportRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    // 게시글 신고
    suspend fun reportPost(
        uid: String,
        postId: Int,
        reportReason: String
    ): HttpsCallableResult {
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("postId", postId)
            put("reportReason", reportReason)
        }

        return functions.getHttpsCallable(Contents.FUNC_REPORT_POST)
            .call(jsonData)
            .await()!!
    }
}