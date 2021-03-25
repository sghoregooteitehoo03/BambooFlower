package com.sg.android.bambooflower.viewmodel.createAccountFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreateAccountRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    fun createAccount(id: String, password: String) =
        auth.createUserWithEmailAndPassword(id, password)

    suspend fun setUserData(name: String) {
        val token = auth.currentUser?.getIdToken(true)
            ?.await()
            ?.token

        val userData = User(
            uid = auth.currentUser?.uid,
            name = name,
            token = token,
            achievedCount = 0,
            myLevel = 1,
            myMission = null,
            latestStart = 0,
            isAchieved = false,
            missionDoc = null
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