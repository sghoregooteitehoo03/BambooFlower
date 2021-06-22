package com.sg.android.bambooflower.viewmodel.createAccountFragment

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
            achieveState = User.STATE_NOTHING,
            myLevel = 1,
            myMissionTitle = "",
            myMissionHow = "",
            latestStart = 0,
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
            achieveState = User.STATE_NOTHING,
            myLevel = 1,
            myMissionTitle = "",
            myMissionHow = "",
            latestStart = 0,
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

    suspend fun signOut(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        val loginManager = LoginManager.getInstance()

        googleClient.signOut()
            .await()
        loginManager.logOut()

        auth.signOut()
    }
}