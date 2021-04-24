package com.sg.android.bambooflower.viewmodel.postFragment

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    // 응원하기
    suspend fun cheerUp(uid: String, postData: Post): Boolean {
        val postRef = store.collection(Contents.COLLECTION_POST)
            .document(postData.postPath!!)
        var isCheerUp = false

        store.runTransaction { transition ->
            val currentData = transition[postRef].toObject(Post::class.java)!!

            if (postData.favorites.contains(uid)) { // 이미 응원을 하였을 때
                currentData.favoriteCount -= 1
                currentData.favorites.remove(uid)

                isCheerUp = false
            } else { // 응원을 하지 않았을 때
                currentData.favoriteCount += 1
                currentData.favorites[uid] = true

                isCheerUp = true
            }
            postData.favorites = currentData.favorites
            postData.favoriteCount = currentData.favoriteCount

            transition.set(postRef, currentData)
        }.await()

        return isCheerUp
    }

    // 게시글 삭제
    suspend fun deletePost(postData: Post) {
        for (i in postData.image!!.indices) { // storage에 저장된 이미지를 차례대로 삭제함
            val imageName = "${postData.postPath}-$i"
            val reference = storage.reference
                .child(Contents.CHILD_POST_IMAGE)
                .child(postData.uid!!)
                .child(imageName)

            reference.delete()
                .await()
        }

        store.collection(Contents.COLLECTION_POST) // firestore에 저장된 데이터를 삭제함
            .document(postData.postPath!!)
            .delete()
            .await()
    }
}