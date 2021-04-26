package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddPostRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    // 게시글 작성
    suspend fun addPost(
        title: String,
        content: String,
        images: List<Uri>,
        user: User
    ): Task<Void> {
        val currentTime = System.currentTimeMillis()
        val uid = auth.currentUser?.uid!!
        val docPath = "$currentTime-$uid" // 게시글 문서 위치

        val imagePath = mutableListOf<String>() // 이미지 위치

        for (i in images.indices) {
            val image = images[i]
            val imageName = "$i.png" // 이미지 이름

            val reference = storage.reference // 저장될 경로
                .child(uid)
                .child(Contents.CHILD_POST_IMAGE)
                .child(docPath)
                .child(imageName)

            reference.putFile(image)
                .await()
            imagePath.add(reference.downloadUrl.await().toString()) // 이미지 위치 저장
        }

        val postData = Post(
            title = title,
            contents = content,
            image = imagePath,
            viewCount = 0,
            favoriteCount = 0,
            timeStamp = currentTime,
            uid = uid,
            writer = user.name,
            postPath = docPath
        )

        return store.collection(Contents.COLLECTION_POST) // 게시글 작성
            .document(docPath)
            .set(postData)
    }
}