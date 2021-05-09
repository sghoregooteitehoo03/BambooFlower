package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import javax.inject.Inject

class AddPostRepository @Inject constructor(
    private val functions: FirebaseFunctions,
    private val storage: FirebaseStorage
) {
    // 게시글 작성
    suspend fun addPost(
        title: String,
        content: String,
        images: List<Uri>,
        user: User,
        contentResolver: ContentResolver
    ): HttpsCallableResult? {
        // TODO: 서버리스 에서 동작되게 구현 O
        val currentTime = System.currentTimeMillis() // 현재 시간
        val uid = user.uid!!
        val docPath = "$currentTime-$uid" // 게시글 문서 위치

        val imagePath = mutableListOf<String>() // 이미지 위치

        // storage에 저장
        for (i in images.indices) {
            val image = imageSizeConvert(images[i], contentResolver)
            val imageName = "$i.png" // 이미지 이름

            val reference = storage.reference // 저장될 경로
                .child(uid)
                .child(Contents.CHILD_POST_IMAGE)
                .child(docPath)
                .child(imageName)

            reference.putFile(image)
                .await()
            imagePath.add(reference.downloadUrl.await().toString()) // 이미지 위치 저장

            contentResolver.delete(image, null, null) // 생성한 이미지를 다시 삭제함
        }

        // 나머지는 Functions에서 처리
        val imagesJson = JSONArray().apply {
            for (image in imagePath) {
                put(image)
            }
        }
        val jsonObj = JSONObject().apply {
            put("title", title)
            put("content", content)
            put("images", imagesJson)
            put("user", Gson().toJson(user))
            put("currentTime", currentTime)
        }

        return functions.getHttpsCallable(Contents.FUNC_SUCCESS_MISSION)
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
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream) // 이미지 압축

        // bitmap -> uri
        val bytes = outputStream.toByteArray()
        imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return MediaStore.Images
            .Media
            .insertImage(contentResolver, imageBitmap, "image", null)
            .toUri()
    }
}