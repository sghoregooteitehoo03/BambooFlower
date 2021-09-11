package com.sg.android.bambooflower.viewmodel.signUpFrag

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.messaging.FirebaseMessaging
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val messaging: FirebaseMessaging
) {
    suspend fun login(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .await()
    }

    suspend fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()
    }

    suspend fun checkUserData(): HttpsCallableResult {
        val uid = auth.currentUser?.uid
        val token = messaging.token
            .await()!!
        val jsonData = JSONObject().apply {
            put("userId", uid)
            put("alarmToken", token)
        }

        return functions.getHttpsCallable(Contents.FUNC_GET_USER_DATA)
            .call(jsonData)
            .await()!!
    }


    fun isLogin() =
        auth.currentUser != null

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