package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomButton
import com.sg.android.bambooflower.ui.view.CustomSettingView
import de.hdodenhof.circleimageview.CircleImageView
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
    view.text = "[$title]"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setRanking")
fun setRanking(view: TextView, rank: Int) {
    view.text = "#$rank"
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setRankingInfo")
fun setRankingInfo(view: TextView, user: User) {
    view.text = "Lv: ${user.myLevel} | 수행한 미션: ${user.achievedCount}"
}

@BindingAdapter("app:setRankingImage")
fun setRankingImage(view: ImageView, rank: Int) {
    when (rank) {
        1 -> {
            view.visibility = View.VISIBLE
            Glide.with(view).load(R.drawable.rank1).into(view)
        }
        2 -> {
            view.visibility = View.VISIBLE
            Glide.with(view).load(R.drawable.rank2).into(view)
        }
        3 -> {
            view.visibility = View.VISIBLE
            Glide.with(view).load(R.drawable.rank3).into(view)
        }
        else -> {
            view.visibility = View.GONE
        }
    }
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
fun searchPosition(view: RecyclerView, position: Int?) {
    if (position != null && position != -1) {
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

@BindingAdapter("app:setUriImage", "app:setResourceImage", "app:setBitmapImage", requireAll = false)
fun setUriImage(view: ImageView, imageUri: Uri?, imageResource: Int?, imageBitmap: Bitmap?) {
    if (imageUri != null) {
        Glide.with(view.context).load(imageUri).into(view)
    } else if (imageResource != null) {
        Glide.with(view.context).load(imageResource).into(view)
    } else if (imageBitmap != null) {
        Glide.with(view.context).load(imageBitmap).into(view)
    }
    view.clipToOutline = true
}

@BindingAdapter("app:isCheerUp", "app:cheerUpCount")
fun isCheerUp(view: CustomButton, cheerUp: Boolean, count: Int) {
    if (cheerUp) {
        view.setCustomButtonIcon(R.drawable.ic_thumb_up)
        view.setCustomButtonText(count.toString())
    } else {
        view.setCustomButtonIcon(R.drawable.ic_thumb_up_off)
        view.setCustomButtonText("화이팅")
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setPosition", "app:setSize", requireAll = true)
fun setPosition(view: TextView, pos: Int, size: Int) {
    view.text = "${pos}/${size}"
}

@BindingAdapter("app:setProfileImage")
fun setProfileImage(view: CircleImageView, imageUri: String) {
    if (imageUri.isNotEmpty()) {
        Glide.with(view)
            .load(imageUri)
            .into(view)
    } else {
        Glide.with(view)
            .load(R.drawable.ic_person)
            .into(view)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setLevel")
fun setLevel(view: TextView, level: Int) {
    view.text = "Lv $level"
}

@BindingAdapter("app:setCompleteDate")
fun setCompleteDate(view: TextView, time: Long) {
    val format = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(time)
    view.text = "수행완료: $format"
}

@BindingAdapter("app:hideKeyboard")
fun hideKeyboard(view: TextInputEditText, isHide: Boolean) {
    if (isHide) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
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