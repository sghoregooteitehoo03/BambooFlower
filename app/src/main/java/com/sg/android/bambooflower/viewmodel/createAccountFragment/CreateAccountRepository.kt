package com.sg.android.bambooflower.viewmodel.createAccountFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.data.Account
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

    suspend fun setUserData(email: String, password: String, name: String) {
        val uid = auth.currentUser?.uid!!
        val account = Account(
            loginWay = "Email",
            email = email,
            password = password
        )
        val userData = User(
            uid = uid,
            name = name,
            profileImage = "",
            email = email,
            achievedCount = 0,
            myLevel = 1,
            myMission = null,
            latestStart = 0,
            achieved = false,
            missionDoc = null
        )

        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .set(userData)
            .await()

        store.collection(Contents.COLLECTION_ACCOUNT)
            .document(uid)
            .set(account)
            .await()
    }

    suspend fun setUserData(email: String, name: String, token: String, loginWay: String) {
        val uid = auth.currentUser?.uid!!
        val account = Account(
            loginWay = loginWay,
            email = email,
            token = token
        )
        val userData = User(
            uid = uid,
            name = name,
            profileImage = "",
            email = email,
            achievedCount = 0,
            myLevel = 1,
            myMission = null,
            latestStart = 0,
            achieved = false,
            missionDoc = null
        )

        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .set(userData)
            .await()

        store.collection(Contents.COLLECTION_ACCOUNT)
            .document(uid)
            .set(account)
            .await()
    }
}