package com.sg.android.bambooflower.viewmodel.signUpFrag

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore
) {
    suspend fun login(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .await()
    }

    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()
    }

    fun getUserData() =
        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .get()

    fun isLogin() =
        auth.currentUser != null

    fun checkServer() =
        store.collection(Contents.COLLECTION_SERVER)
            .document("Important")
            .get()

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