package com.sg.android.bambooflower.viewmodel.addPostFragment

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableResult
import com.sg.android.bambooflower.data.Quest
import com.sg.android.bambooflower.other.Contents
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class AddPostRepository @Inject constructor(
    private val functions: FirebaseFunctions,
) {
    // 게시글 작성
    suspend fun addPost(
        content: String,
        imageUri: Uri,
        uid: String,
        quest: Quest,
        contentResolver: ContentResolver
    ): HttpsCallableResult {
        // 압축 된 이미지
        val image = imageSizeConvert(imageUri, contentResolver)
        val jsonObject = JSONObject().apply {
            put("title", quest.title)
            put("content", content)
            put("image", image)
            put("userId", uid)
            put("questId", quest.id)
        }

        // TODO: 나중에 수정
        return functions.getHttpsCallable("addPostTest")
            .call(jsonObject)
            .await()!!
    }

    // 기존 이미지를 압축하여 새로운 이미지를 만듬
    private fun imageSizeConvert(uri: Uri, contentResolver: ContentResolver): String {
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

        // byte -> string
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}