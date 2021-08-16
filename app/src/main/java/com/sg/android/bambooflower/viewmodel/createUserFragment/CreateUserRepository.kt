package com.sg.android.bambooflower.viewmodel.createUserFragment

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class CreateUserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val functions: FirebaseFunctions
) {

    suspend fun setUserData(
        profileImageUri: Uri?,
        email: String,
        name: String,
        token: String,
        loginWay: String,
        contentResolver: ContentResolver
    ): HttpsCallableResult {
        val uid = auth.currentUser?.uid!!
        val profileImage = if (profileImageUri != null) { // 프로필 이미지를 선택한 경우
            val bytes = imageSizeConvert(profileImageUri, contentResolver) // 압축된 이미지 바이트를 가져옴
            JSONArray().apply { // json 배열로 변환
                for(byte in bytes) {
                    put(byte)
                }
            }
        } else {
            null
        }
        val loginToken = "${loginWay}-${token}"

        // json 데이터로 변환
        val jsonData = JSONObject().apply {
            put("uid", uid)
            put("name", name)
            put("email", email)
            put("profileImage", profileImage)
            put("loginToken", loginToken)
        }

        // 유저 생성
        return functions.getHttpsCallable(Contents.FUNC_CREATE_USER)
            .call(jsonData)
            .await()!!
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

    // 기존 이미지를 압축하여 바이트로 반환함
    private fun imageSizeConvert(uri: Uri, contentResolver: ContentResolver): ByteArray {
        // uri -> bitmap
        val imageBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        // 이미지 압축
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        return outputStream.toByteArray()
    }
}