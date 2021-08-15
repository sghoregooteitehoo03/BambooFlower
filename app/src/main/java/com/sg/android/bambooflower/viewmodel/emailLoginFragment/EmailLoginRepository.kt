package com.sg.android.bambooflower.viewmodel.emailLoginFragment

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmailLoginRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    suspend fun login(credential: AuthCredential) =
        auth.signInWithCredential(credential)
            .await()!!

    fun checkUserData() =
        functions.getHttpsCallable(Contents.FUNC_GET_USER_DATA)
            .call(auth.currentUser?.uid!!)
}