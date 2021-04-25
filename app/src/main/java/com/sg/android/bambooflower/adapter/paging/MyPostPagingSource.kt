package com.sg.android.bambooflower.adapter.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await

class MyPostPagingSource(private val store: FirebaseFirestore, private val uid: String) :
    PagingSource<QuerySnapshot, Post>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Post>): QuerySnapshot? {
        return state.closestPageToPosition(0)
            ?.prevKey
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Post> {
        return try {
            val currentPage = params.key ?: store.collection(Contents.COLLECTION_POST)
                .whereEqualTo("uid", uid)
                .limit(10)
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = store.collection(Contents.COLLECTION_POST)
                .whereEqualTo("uid", uid)
                .limit(10)
                .startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(Post::class.java),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}