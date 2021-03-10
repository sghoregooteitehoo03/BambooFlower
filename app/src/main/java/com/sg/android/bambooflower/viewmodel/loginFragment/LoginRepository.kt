package com.sg.android.bambooflower.viewmodel.loginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)
}