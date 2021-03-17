package com.sg.android.bambooflower.viewmodel.homeFragment

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {
    fun getHomeData() =
        functions.getHttpsCallable(Contents.FUNC_GET_HOME_DATA)
            .call(auth.currentUser?.uid)

    fun successMission() =
        functions.getHttpsCallable(Contents.FUNC_SUCCESS_MISSION)
            .call(auth.currentUser?.uid)
}