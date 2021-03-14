package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailLoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)

    suspend fun setUserData() {
        val token = auth.currentUser?.getIdToken(true)
            ?.await()
            ?.token

        val userData = User(
            name = auth.currentUser?.displayName,
            token = token,
            achievedCount = 0,
            myLevel = 1,
            myMission = null,
            latestStart = 0
        )

        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .set(userData)
            .await()
    }

    suspend fun getUserData() =
        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .get()
            .await()!!
}