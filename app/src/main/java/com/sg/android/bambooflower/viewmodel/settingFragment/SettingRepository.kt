package com.sg.android.bambooflower.viewmodel.settingFragment

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.database.DiaryDao
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SettingRepository @Inject constructor(
    private val dao: DiaryDao,
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    // 일기 모두삭제
    suspend fun clearDiary(uid: String?) {
        dao.clearDiary(uid)
    }

    suspend fun signOut(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val googleClient = GoogleSignIn.getClient(context, gso)
        val loginManager = LoginManager.getInstance()

        auth.signOut()
        googleClient.signOut()
            .await()
        loginManager.logOut()
    }
}