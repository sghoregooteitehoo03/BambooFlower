package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EmailLoginRepository @Inject constructor(private val auth: FirebaseAuth) {
    fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)
}