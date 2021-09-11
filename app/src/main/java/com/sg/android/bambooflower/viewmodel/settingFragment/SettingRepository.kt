package com.sg.android.bambooflower.viewmodel.settingFragment

import android.content.Context
import android.content.SharedPreferences
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class SettingRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @Named(Contents.PREF_SETTING) private val settingPref: SharedPreferences
) {
    fun getSettingPref() =
        settingPref

    fun setSettingPref(isSwitched: Boolean, prefKey: String) {
        with(settingPref.edit()) {
            putBoolean(prefKey, isSwitched)
            commit()
        }
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