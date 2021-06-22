package com.sg.android.bambooflower.viewmodel.acceptListDialog

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.adapter.paging.AcceptPagingSource
import javax.inject.Inject

class AcceptListRepository @Inject constructor(private val store: FirebaseFirestore) {

    fun getAcceptList(postPath: String) =
        Pager(PagingConfig(20)) {
            AcceptPagingSource(store, postPath)
        }
}