package com.sg.android.bambooflower.viewmodel.resetPasswordFrag

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ResetPasswordRepository @Inject constructor(private val auth: FirebaseAuth) {

    suspend fun sendEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .await()
    }
}