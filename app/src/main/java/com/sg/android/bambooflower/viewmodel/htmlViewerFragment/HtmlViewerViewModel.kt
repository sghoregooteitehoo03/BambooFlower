package com.sg.android.bambooflower.viewmodel.htmlViewerFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HtmlViewerViewModel @Inject constructor(private val repository: HtmlViewerRepository) : ViewModel() {
    private val _url = MutableLiveData("")
    val url: LiveData<String> = _url

    val isLoaded = MutableLiveData(true)

    fun readHtml(title: String) = viewModelScope.launch {
        Log.i("Check", "title: $title")
        _url.value = try {
            repository.readHtml(title).toString()
        } catch (e: Exception) {
            ""
        }
    }
}