package com.sg.android.bambooflower.viewmodel.editProfileFrag

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: EditProfileRepository
) : ViewModel() {
    private val _errorEmailMsg = MutableLiveData("") //  이메일 에러 메시지
    private val _isLoading = MutableLiveData(false) // 로딩 여부
    private val _isUpdate = MutableLiveData(false) // 업데이트 여부

    val errorEmailMsg: LiveData<String> = _errorEmailMsg
    val isLoading: LiveData<Boolean> = _isLoading
    val isUpdate: LiveData<Boolean> = _isUpdate

    val profileImage = MutableLiveData("") // 프로필 이미지
    val email = MutableLiveData("") // 이메일
    val name = MutableLiveData("") // 닉네임
    val isError = MutableLiveData(false) // 에러 여부

    fun changeProfile(user: User) = viewModelScope.launch {
        _isLoading.value = true // 로딩 시작
        _errorEmailMsg.value = ""

        // 이메일 형식이 맞는지 확인
        if (checkEmail()) {
            try {
                val result = repository.changeProfile(
                    user.uid,
                    profileImage.value!!,
                    email.value!!,
                    name.value!!
                ).data as Map<*, *>

                if (result["complete"] == null) { // 오류 확인
                    throw NullPointerException()
                }

                user.profileImage = profileImage.value!!
                user.email = email.value!!
                user.name = name.value!!

                _isUpdate.value = true // 업데이트 성공
            } catch (e: Exception) {
                isError.value = true
            }
        } else {
            _errorEmailMsg.value = ErrorMessage.NOT_EMAIL_TYPE
        }

        _isLoading.value = false // 로딩 끝
    }

    // 이메일 형식 확인
    private fun checkEmail() =
        Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()

    // 갤러리에서 가져온 uri데이터 사진을 string으로 변환해서 저장
    fun setProfileImageToString(image: Uri, contentResolver: ContentResolver) {
        // uri -> bitmap
        val imageBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, image)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, image)
        }

        // bitmap -> byte
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)

        // byte -> string
        profileImage.value = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}