package com.sg.android.bambooflower.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.sg.android.bambooflower.data.Accept
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await

class AcceptPagingSource(private val store: FirebaseFirestore, private val postPath: String) :
    PagingSource<QuerySnapshot, Accept>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Accept>): QuerySnapshot? {
        return state.closestPageToPosition(0)
            ?.prevKey
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Accept> {
        return try {
            val currentPage = params.key ?: store.collection(Contents.COLLECTION_ACCEPT)
                .whereEqualTo("postPath", postPath)
                .limit(10)
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = store.collection(Contents.COLLECTION_ACCEPT)
                .whereEqualTo("postPath", postPath)
                .limit(10)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            val list = currentPage.toObjects(Accept::class.java)
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}