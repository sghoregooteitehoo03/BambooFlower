package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Diary
import com.sg.android.bambooflower.data.Mission
import com.sg.android.bambooflower.data.Post
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomButton
import com.sg.android.bambooflower.ui.view.CustomMissionButton
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

//Set
@SuppressLint("SetTextI18n")
@BindingAdapter("app:setPostItemInfo")
fun setPostItemInfo(view: TextView, postData: Post?) {
    if (postData != null) {
        val name = postData.writer
        val postTime = postData.timeStamp!!

        val diffTime = System.currentTimeMillis() - postTime
        val date = when {
            diffTime / 3600000 >= 24 -> {
                SimpleDateFormat("yy.MM.dd", Locale.KOREA).format(postTime)
            }
            diffTime / 3600000 != 0.toLong() -> {
                "${diffTime / 3600000}시간 전"
            }
            diffTime / 60000 != 0.toLong() -> {
                "${diffTime / 60000}분 전"
            }
            else -> {
                "${diffTime / 6000}초 전"
            }
        }

        view.text = "$name | $date"
    }
}

@BindingAdapter("app:setSelectMission")
fun setSelectMission(view: ConstraintLayout, isSelected: Boolean) {
    if (isSelected) {
        view.setBackgroundResource(R.drawable.shape_mission_text)
    } else {
        view.setBackgroundResource(0)
    }
}

@BindingAdapter("app:setMissionCompleteLayout", "app:setMissionTodayLayout", requireAll = true)
fun setMissionLayout(view: LinearLayout, mission: Mission, user: User) {
    view.visibility = View.VISIBLE

    if (mission.document == user.missionDoc) { // 유저가 수행중인 미션일 때
        view.setBackgroundResource(R.color.deep_orange_300)
    } else if (mission.complete.containsKey(user.uid)) { // 유저가 수행완료 한 미션일 때
        view.setBackgroundResource(R.color.green_300)
    } else {
        view.setBackgroundResource(R.color.gray)
    }
}

@BindingAdapter("app:setMissionCompleteText", "app:setMissionTodayText", requireAll = true)
fun setMissionText(view: TextView, mission: Mission, user: User) {
    view.text = if (mission.document == user.missionDoc) { // 유저가 수행중인 미션일 때
        "오늘 미션"
    } else if (mission.complete.containsKey(user.uid)) { // 유저가 수행완료 한 미션일 때
        "수행 완료"
    } else {
        "미수행"
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setLastMissions")
fun setLastMissions(view: TextView, achievedCount: Int?) {
    if (achievedCount != null) {
        view.text = "다음 레벨까지 남은 미션: ${10 - (achievedCount % 10)}"
    }
}

@BindingAdapter("app:setButtonStateUser", "app:setButtonStateMission", requireAll = true)
fun setButtonState(view: CustomMissionButton, user: User?, mission: Mission?) {
    if (user != null && mission != null) {
        if (mission.document != user.missionDoc) {
            // 유저가 수행중인 미션과 다를 경우
            val timestamp = mission.complete[user.uid]

            if (timestamp != null) {
                // 수행 완료한 미션일 경우
                val successDay = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                    .format(timestamp)

                with(view) {
                    setButtonText("수행 완료: $successDay", Color.WHITE)
                    setButtonIcon(R.drawable.ic_check_circle)

                    backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.green_300)
                }
            } else {
                // 수행하지 않은 미션일 경우
                with(view) {
                    setButtonText(
                        "미수행",
                        view.resources.getColor(android.R.color.tab_indicator_text, null)
                    )
                    setButtonIcon(R.drawable.ic_baseline_block)

                    backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.gray)
                }
            }
        } else {
            // 유저가 수행하고 있는 미션일 경우
            if (user.achieveState == User.STATE_LOADING) {
                // 유저 상태가 "인증 중"일 떄
                with(view) {
                    setButtonText("인증 중...", Color.WHITE)
                    setButtonIcon(R.drawable.ic_cancel_white)

                    backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.green_300)
                }
            } else if (user.achieveState == User.STATE_ALLOW) {
                // 유저 상태가 "인증 완료"일 떄
                with(view) {
                    setButtonText("인증 완료", Color.WHITE)
                    setButtonIcon(R.drawable.ic_check_circle)

                    backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.green_300)
                }
            }
        }
    }
}

@BindingAdapter("app:setPostInfo")
fun setPostInfo(view: TextView, postData: Post) {
    val name = postData.writer
    val date = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        .format(postData.timeStamp)

    view.text = "$name | $date"
}

@BindingAdapter("app:setCalendar")
fun setCalendar(view: TextView, diaryData: Diary) {
    val date = SimpleDateFormat("yy.MM.dd (EE)", Locale.KOREA).format(diaryData.timeStamp)

    view.text = date
}

@BindingAdapter("app:setSelectedText")
fun setSelectedText(view: TextView, isSelected: Boolean) {
    if (isSelected) {
        view.setTextColor(view.resources.getColor(R.color.default_item_color, null))
        view.setTypeface(view.typeface, Typeface.BOLD)
        view.textSize = 16f
    } else {
        view.setTextColor(view.resources.getColor(android.R.color.tab_indicator_text, null))
        view.setTypeface(view.typeface, Typeface.NORMAL)
        view.textSize = 14f
    }
}

@BindingAdapter("app:setYearText")
fun setYearText(view: TextView, timeStamp: Long) {
    view.text = SimpleDateFormat("yyyy", Locale.KOREA).format(timeStamp)
}

@BindingAdapter("app:setMonthDayText")
fun setMonthDayText(view: TextView, timeStamp: Long) {
    view.text = SimpleDateFormat("MM월 dd(EE)", Locale.KOREA).format(timeStamp)
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setTitle")
fun setTitle(view: TextView, title: String?) {
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
fun searchPosition(view: RecyclerView, position: Int) {
    if (position != -1) {
        view.scrollToPosition(position)
    }
}

@BindingAdapter("app:setUriImage", "app:setResourceImage", "app:setBitmapImage", "app:setPostImage", requireAll = false)
fun setUriImage(view: ImageView, imageUri: Uri?, imageResource: Int?, imageBitmap: Bitmap?, postData: Post?) {
    if (imageUri != null) {
        Glide.with(view.context).load(imageUri).into(view)
    } else if (imageResource != null) {
        Glide.with(view.context).load(imageResource).into(view)
    } else if (imageBitmap != null) {
        Glide.with(view.context).load(imageBitmap).into(view)
    } else if (postData != null) {
        Glide.with(view.context).load(postData.image!![0]).into(view)
    }
    view.clipToOutline = true
}

@BindingAdapter("app:isCheerUp", "app:cheerUpCount")
fun isCheerUp(view: CustomButton, cheerUp: Boolean, count: Int) {
    view.setCustomButtonText(count.toString())

    if (cheerUp) {
        view.setCustomButtonIcon(R.drawable.ic_thumb_up)
    } else {
        view.setCustomButtonIcon(R.drawable.ic_thumb_up_off)
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

@BindingAdapter("app:loadBannerAd")
fun loadAd(view: AdView, isLoad: Boolean) {
    val request = AdRequest.Builder().build()
    view.loadAd(request)
}

@BindingAdapter("app:loadUrl")
fun loadUrl(view: WebView, url: String) {
    if (url.isNotEmpty()) {
        view.loadUrl(url)
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