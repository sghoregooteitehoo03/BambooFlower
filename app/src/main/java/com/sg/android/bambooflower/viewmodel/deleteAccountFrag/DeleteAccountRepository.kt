package com.sg.android.bambooflower.viewmodel.deleteAccountFrag

import android.content.Context
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.database.DiaryDao
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DeleteAccountRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val store: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val dao: DiaryDao
) {

    // 재로그인
    suspend fun reLogin(user: User, password: String) {
        val credential = when (user.loginWay!!) {
            "Email" -> {
                EmailAuthProvider.getCredential(auth.currentUser?.email!!, password)
            }
            "Google" -> {
                GoogleAuthProvider.getCredential(user.token, null)
            }
            "Facebook" -> {
                FacebookAuthProvider.getCredential(user.token)
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

    suspend fun deleteAccount(user: User, context: Context) {
        val uid = user.uid!!

        // 일기 모두삭제
        dao.clearDiary(uid)

        // 유저 데이터 삭제
        store.collection(Contents.COLLECTION_USER)
            .document(uid)
            .delete()
            .await()

        // 유저 프로필 사진 삭제
        if (user.profileImage.isNotEmpty()) {
            storage.reference.child(uid)
                .child("profile.png")
                .delete()
                .await()
        }

        // 유저의 게시글 및 사진 삭제
        val postList = store.collection(Contents.COLLECTION_POST)
            .whereEqualTo("uid", uid)
            .get()
            .await()

        postList.forEach {
            val postData = it.toObject(Post::class.java)

            // 게시글에 사진을 삭제함
            val imageName = "${postData.timeStamp}.png"
            val reference = storage.reference
                .child(uid)
                .child(Contents.CHILD_POST_IMAGE)
                .child(postData.postPath!!)
                .child(imageName)

            reference.delete()
                .await()

            // 게시글 데이터 삭제
            it.reference.delete()
                .await()
        }

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