package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomButton
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setInfo")
fun setInfo(view: TextView, postData: Post) {
    val name = postData.writer
    val date = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(postData.timeStamp)

    view.text = "$name | $date"
}

@BindingAdapter("app:setCalendar")
fun setCalendar(view: TextView, diaryData: Diary) {
    val date = SimpleDateFormat("yyyy.MM.dd (EE)", Locale.KOREA).format(diaryData.timeStamp)

    view.text = date
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setRanking")
fun setRanking(view: TextView, position: Int) {
    view.text = "#$position"
}

@BindingAdapter("app:errorMessage")
fun errorMessage(view: TextView, errorMsg: String) {
    if (errorMsg.isNotEmpty() && errorMsg != ErrorMessage.SUCCESS) {
        view.visibility = View.VISIBLE
        view.text = errorMsg
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@BindingAdapter("app:buttonEnabled")
fun buttonEnabled(view: CustomButton, isEnabled: Boolean) {
    view.setCustomEnabled(isEnabled)
}