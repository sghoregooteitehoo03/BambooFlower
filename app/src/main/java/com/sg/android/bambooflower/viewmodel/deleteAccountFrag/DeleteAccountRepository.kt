package com.sg.android.bambooflower.viewmodel.deleteAccountFrag

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeleteAccountRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions,
    private val dao: DiaryDao
) {

    // 재로그인
    suspend fun reLogin(loginTokenSplit: List<String>, password: String) {
        val loginWay = loginTokenSplit[0]
        val token = loginTokenSplit[1]
        val credential = when (loginWay) {
            "Email" -> {
                EmailAuthProvider.getCredential(auth.currentUser?.email!!, password)
            }
            "Google" -> {
                GoogleAuthProvider.getCredential(token, null)
            }
            "Facebook" -> {
                FacebookAuthProvider.getCredential(token)
            }
            else -> {
                null
            }
        }

        credential?.let {
            auth.currentUser // 재인증
                ?.reauthenticate(credential)
                ?.await()
        }
    }

    suspend fun deleteUserDb(uid: String): HttpsCallableResult =
        functions.getHttpsCallable(Contents.FUNC_DELETE_USER_DATA)
            .call(uid)
            .await()!!

    suspend fun deleteAccount(uid: String, context: Context) {
        // 일기 모두삭제
        dao.clearDiary(uid)

        auth.currentUser // 계정 삭제
            ?.delete()
            ?.await()
        signOut(context) // 로그아웃
    }

    // 로그아웃
    private suspend fun signOut(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        val loginManager = LoginManager.getInstance()

        googleClient.signOut()
            .await()
        loginManager.logOut()
    }
}