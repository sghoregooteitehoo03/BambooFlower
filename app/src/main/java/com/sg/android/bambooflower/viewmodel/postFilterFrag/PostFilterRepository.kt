package com.sg.android.bambooflower.viewmodel.postFilterFrag

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.MyPostPagingSource
import com.sg.android.bambooflower.adapter.paging.PostFilterPagingSource
import com.sg.android.bambooflower.adapter.paging.PostPagingSource
import javax.inject.Inject

class PostFilterRepository @Inject constructor(
    private val store: FirebaseFirestore
) {
    fun getPostList() =
        Pager(PagingConfig(20)) {
            PostPagingSource(store)
        }

    fun getPostFilterList() =
        Pager(PagingConfig(20)) {
            PostFilterPagingSource(store)
        }

    fun getMyPostList(uid: String) =
        Pager(PagingConfig(20)) {
            MyPostPagingSource(store, uid)
        }
}