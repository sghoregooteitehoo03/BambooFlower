package com.sg.android.bambooflower.viewmodel.postFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.PostPagingSource
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun getPostList() =
        Pager(PagingConfig(20)) {
            PostPagingSource(store)
        }
}