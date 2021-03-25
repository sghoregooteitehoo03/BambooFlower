package com.sg.android.bambooflower.viewmodel.rankingFragment

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.UserPagingSource
import javax.inject.Inject

class RankingRepository @Inject constructor(private val store: FirebaseFirestore) {
    fun getRankingList() =
        Pager(config = PagingConfig(pageSize = 20)) {
            UserPagingSource(store)
        }
}