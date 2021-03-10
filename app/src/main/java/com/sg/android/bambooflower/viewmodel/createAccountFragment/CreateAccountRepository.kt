package com.sg.android.bambooflower.viewmodel.createAccountFragment

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class CreateAccountRepository @Inject constructor(private val auth: FirebaseAuth) {
    fun createAccount(id: String, password: String) =
        auth.createUserWithEmailAndPassword(id, password)
}