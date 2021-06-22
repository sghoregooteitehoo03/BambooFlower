package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class EmailLoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)

    fun getUserData() =
        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .get()
}