package com.sg.android.bambooflower.viewmodel.htmlViewerFragment

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HtmlViewerRepository @Inject constructor(private val storage: FirebaseStorage) {

    suspend fun readHtml(title: String): Uri {
        return storage.reference
            .child(title)
            .downloadUrl
            .await()!!
    }
}