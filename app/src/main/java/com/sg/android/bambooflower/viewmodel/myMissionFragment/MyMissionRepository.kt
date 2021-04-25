package com.sg.android.bambooflower.viewmodel.myMissionFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.MyMissionPagingSource
import javax.inject.Inject

class MyMissionRepository @Inject constructor(private val store: FirebaseFirestore) {

    fun getMyMission(uid: String) =
        Pager(PagingConfig(20)) {
            MyMissionPagingSource(store, uid)
        }
}