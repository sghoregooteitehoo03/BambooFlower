package com.sg.android.bambooflower.viewmodel.albumFragment

import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sg.android.bambooflower.data.Album
import kotlinx.coroutines.launch

class AlbumViewModel : ViewModel() {
    private val _album = MutableLiveData<List<Album>>()

    val album: LiveData<List<Album>> = _album // 모든 이미지
    val selectedImage = MutableLiveData<MutableList<String>>(mutableListOf()) // 이전에 선택한 이미지

    // 모든 이미지를 가져옴
    fun getAllImage(cursor: Cursor) = viewModelScope.launch {
        val albumList = mutableListOf<Album>()
        while (cursor.moveToNext()) {
            val id = cursor.getLong(
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            )
            val imageUri = Uri.withAppendedPath(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id.toString()
            )

            val albumData =
                if (selectedImage.value!!.contains(imageUri.toString())) { // 이미 선택된 이미지인지 확인
                    Album(imageUri, true)
                } else {
                    Album(imageUri)
                }
            albumList.add(albumData)
        }

        _album.value = albumList
        cursor.close()
    }

    fun addImage(uri: String) {
        val addList = selectedImage.value!!.apply {
            this.add(uri)
        }
        selectedImage.value = addList
    }

    fun removeImage(uri: String) {
        val removeList = selectedImage.value!!.apply {
            this.remove(uri)
        }
        selectedImage.value = removeList
    }
}