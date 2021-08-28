package com.sg.android.bambooflower.viewmodel.profileFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val store: FirebaseFirestore
) {

//    fun getMyPostList(uid: String) =
//        Pager(PagingConfig(20)) {
//            MyPostPagingSource(store, uid)
//        }.flow
}