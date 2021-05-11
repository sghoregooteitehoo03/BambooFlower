package com.sg.android.bambooflower.viewmodel.loginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    suspend fun login(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .await()
    }

    fun getUserData() =
        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .get()

    fun isLogin() =
        auth.currentUser != null
}