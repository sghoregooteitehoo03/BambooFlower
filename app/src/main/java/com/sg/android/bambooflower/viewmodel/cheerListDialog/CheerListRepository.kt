package com.sg.android.bambooflower.viewmodel.cheerListDialog

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.adapter.paging.CheerPagingSource
import javax.inject.Inject

class CheerListRepository @Inject constructor(
    private val functions: FirebaseFunctions
) {

    fun getAcceptList(postId: Int) =
        Pager(PagingConfig(20)) {
            CheerPagingSource(functions, postId)
        }.flow
}