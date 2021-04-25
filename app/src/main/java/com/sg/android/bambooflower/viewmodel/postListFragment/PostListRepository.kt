package com.sg.android.bambooflower.viewmodel.postListFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.MyPostPagingSource
import com.sg.android.bambooflower.adapter.paging.PostPagingSource
import javax.inject.Inject

class PostListRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun getPostList() =
        Pager(PagingConfig(20)) {
            PostPagingSource(store)
        }

    fun getMyPostList(uid: String) =
        Pager(PagingConfig(20)) {
            MyPostPagingSource(store, uid)
        }
}