package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sg.android.bambooflower.adapter.PostPagingAdapter
import com.sg.android.bambooflower.data.Post
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setInfo")
fun setInfo(view: TextView, postData: Post) {
    val name = postData.writer
    val date = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(postData.timeStamp)

    view.text = "$name | $date"
}