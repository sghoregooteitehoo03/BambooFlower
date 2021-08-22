package com.sg.android.bambooflower.viewmodel.questDialog

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuestRepository @Inject constructor(
    private val store: FirebaseFirestore
) {

    suspend fun getOtherUserImages(missionDoc: String): List<Uri> {
        val imageList = mutableListOf<Uri>()
        val postList = store.collection(Contents.COLLECTION_POST)
            .whereEqualTo("missionDoc", missionDoc)
            .limit(4)
            .get()
            .await()
            .toObjects(Post::class.java)

        postList.forEach {
            imageList.add(it.image!!.toUri())
        }

        return imageList
    }
}