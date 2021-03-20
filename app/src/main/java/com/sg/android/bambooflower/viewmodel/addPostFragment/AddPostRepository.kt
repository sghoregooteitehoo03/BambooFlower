package com.sg.android.bambooflower.viewmodel.addPostFragment

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class AddPostRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun addPost(title: String, content: String, user: User): Task<Void> {
        val currentTime = System.currentTimeMillis()
        val uid = auth.currentUser?.uid
        val postData = Post(
            title = title,
            contents = content,
            viewCount = 0,
            favoriteCount = 0,
            timeStamp = currentTime,
            writer = user.name,
            uid = uid
        )

        return store.collection(Contents.COLLECTION_POST)
            .document("$currentTime-$uid")
            .set(postData)
    }
}