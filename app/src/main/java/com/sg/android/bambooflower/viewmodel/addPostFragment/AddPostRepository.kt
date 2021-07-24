package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AddPostRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val storage: FirebaseStorage
) {
    // 게시글 작성
    suspend fun addPost(
        content: String,
        imageUri: Uri,
        user: User,
        contentResolver: ContentResolver
    ): HttpsCallableResult? {
        val currentTime = System.currentTimeMillis() // 현재 시간
        val uid = user.uid!! // 유저 아이디
        val docPath = "$currentTime-$uid" // 게시글 문서 위치

        // 압축 된 이미지
        val image = imageSizeConvert(imageUri, contentResolver)
        val imageName = "${currentTime}.png" // 이미지 이름

        val reference = storage.reference // 저장될 경로
            .child(uid)
            .child(Contents.CHILD_POST_IMAGE)
            .child(docPath)
            .child(imageName)

        // storage에 저장
        reference.putFile(image)
            .await()

        // 이미지 위치 저장
        val imagePath = reference.downloadUrl.await().toString()
        contentResolver.delete(image, null, null) // 생성 된 이미지를 다시 삭제함

        // 나머지는 Functions에서 처리
        val jsonObj = JSONObject().apply {
            put("content", content)
            put("image", imagePath)
            put("user", Gson().toJson(user))
            put("currentTime", currentTime)
        }

        return functions.getHttpsCallable(Contents.FUNC_ADD_POST)
            .call(jsonObj)
            .await()
    }

    // 기존 이미지를 압축하여 새로운 이미지를 만듬
    private fun imageSizeConvert(uri: Uri, contentResolver: ContentResolver): Uri {
        // uri -> bitmap
        var imageBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }

        // 이미지 압축
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        // bitmap -> uri
        val bytes = outputStream.toByteArray()
        imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return MediaStore.Images
            .Media
            .insertImage(contentResolver, imageBitmap, "image", null)
            .toUri()
    }
}