package com.sg.android.bambooflower.viewmodel.createUserFragment

import android.content.Context
import androidx.core.net.toUri
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.Account
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CreateUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    suspend fun setUserData(
        profileImage: String,
        account: Account,
        name: String
    ) {
        val uid = auth.currentUser?.uid!!
        val imageName = "profile.png"
        val storageRef = storage.reference
            .child(uid)
            .child(imageName)

        val profileImageUri = if (profileImage.isNotEmpty()) {
            // storage에 프로필 이미지 추가
            storageRef.putFile(profileImage.toUri())
                .await()
            storageRef.downloadUrl
                .await()
                .toString()
        } else {
            ""
        }

        val userData = User(
            uid = uid,
            name = name,
            profileImage = profileImageUri,
            email = account.email,
            achievedCount = 0,
            achieveState = User.STATE_NOTHING,
            myLevel = 1,
            myMissionTitle = "",
            myMissionHow = "",
            latestStart = 0,
            isLevelUp = false,
            missionDoc = null
        )

        store.collection(Contents.COLLECTION_USER)
            .document(auth.currentUser!!.uid)
            .set(userData)
            .await()

        store.collection(Contents.COLLECTION_ACCOUNT)
            .document(uid)
            .set(account)
            .await()
    }

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