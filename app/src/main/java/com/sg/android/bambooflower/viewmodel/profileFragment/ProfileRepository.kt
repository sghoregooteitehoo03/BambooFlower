package com.sg.android.bambooflower.viewmodel.profileFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {

    fun getPreviewPost() =
        store.collection(Contents.COLLECTION_POST)
            .limit(3)
            .whereEqualTo("uid", auth.currentUser?.uid)
            .get()
}