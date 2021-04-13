package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomButton
import java.text.SimpleDateFormat
import java.util.*

//Set
@SuppressLint("SetTextI18n")
@BindingAdapter("app:setInfo")
fun setInfo(view: TextView, postData: Post) {
    val name = postData.writer
    val date = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(postData.timeStamp)

    view.text = "$name | $date"
}

@BindingAdapter("app:setCalendar")
fun setCalendar(view: TextView, diaryData: Diary) {
    val date = SimpleDateFormat("yy.MM.dd (EE)", Locale.KOREA).format(diaryData.timeStamp)

    view.text = date
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setTitle")
fun setTitle(view: TextView, title: String) {
    view.setText("[$title]")
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
@BindingAdapter("app:customButtonEnabled")
fun customButtonEnabled(view: CustomButton, isEnabled: Boolean) {
    view.setCustomEnabled(isEnabled)
}

@BindingAdapter("app:buttonEnabled")
fun buttonEnabled(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}

@BindingAdapter("app:setRefresh")
fun setRefresh(view: SwipeRefreshLayout, isLoading: Boolean) {
    view.isRefreshing = isLoading
}

@BindingAdapter("app:selectDate")
fun selectDate(view: CalendarView, dateTime: Long) {
    view.date = dateTime
}

@BindingAdapter("app:searchPosition")
fun searchPosition(view: RecyclerView, position: Int) {
    if (position != -1) {
        view.scrollToPosition(position + 1)
    }
}

@BindingAdapter("app:leftHour")
fun leftHour(view: TextView, currentTime: Long) {
    val currentHour = SimpleDateFormat("HH", Locale.KOREA)
        .format(currentTime)
        .toInt()
    val leftHour = 24 - currentHour

    view.text = "${leftHour}h 남음"
}

@BindingAdapter("app:setUriImage")
fun setUriImage(view: ImageView, imageUri: Uri) {
    Glide.with(view.context).load(imageUri).into(view)
    view.clipToOutline = true
}

@BindingAdapter("app:setStringImage")
fun setStringImage(view: ImageView, imageStr: String) {
    Glide.with(view.context).load(imageStr).into(view)
    view.clipToOutline = true
}

// Listener
@BindingAdapter("app:setRefreshListener")
fun setRefreshListener(view: SwipeRefreshLayout, listener: InverseBindingListener) {
    view.setOnRefreshListener {
        listener.onChange()
    }
}

@BindingAdapter("app:selectDateListener")
fun selectDateListener(view: CalendarView, listener: InverseBindingListener) {
    view.setOnDateChangeListener { view, year, month, dayOfMonth ->
        val date = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }.timeInMillis
        view.date = date

        listener.onChange()
    }
}

// Get
@InverseBindingAdapter(attribute = "app:setRefresh", event = "app:setRefreshListener")
fun getRefresh(view: SwipeRefreshLayout): Boolean {
    return view.isRefreshing
}

@InverseBindingAdapter(attribute = "app:selectDate", event = "app:selectDateListener")
fun getSelectDate(view: CalendarView): Long {
    return view.date
}