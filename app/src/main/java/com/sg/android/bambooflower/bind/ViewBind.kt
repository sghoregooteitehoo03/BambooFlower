package com.sg.android.bambooflower.bind

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.other.ErrorMessage
import com.sg.android.bambooflower.ui.view.CustomProgressView
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*

//Set
@BindingAdapter("app:setErrorMessageState")
fun setErrorMessageState(view: TextView, isSuccess: Boolean) {
    val color = if (isSuccess) {
        view.resources.getColor(
            R.color.green_500,
            null
        )
    } else {
        view.resources.getColor(
            android.R.color.holo_red_dark,
            null
        )
    }
    view.setTextColor(color)
}

@BindingAdapter("app:setFlowerName")
fun setFlowerName(view: TextView, flowerName: String?) {
    if (flowerName != null) {
        if (flowerName == "씨앗") {
            view.text = "씨앗을 눌러 성장시킬 꽃을 선택해보세요."
            view.textSize = 20f
        } else {
            view.text = flowerName
            view.textSize = 24f
        }
    }
}

@BindingAdapter("app:setFlowerProgress")
fun setFlowerProgress(view: CustomProgressView, progress: Int?) {
    if (progress != null) {
        view.setProgress(progress)
        view.setProgressText("$progress / 100")
    }
}

@BindingAdapter("app:setButtonEnable", "app:setButtonLoadingEnable", requireAll = true)
fun setButtonEnable(view: Button, isEnable: Boolean, loadingEnable: Boolean) {
    if (isEnable && !loadingEnable) { // 버튼 활성화
        view.isEnabled = true
        view.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.green_300)

        view.text = "선택"
    } else { // 버튼 비활성화
        view.isEnabled = false

        if (loadingEnable) {
            view.text = ""
            view.backgroundTintList =
                ContextCompat.getColorStateList(view.context, R.color.green_300)
        } else {
            view.text = "선택"
            view.backgroundTintList =
                ContextCompat.getColorStateList(view.context, R.color.gray)
        }
    }
}

@BindingAdapter("app:setSelectFlower")
fun setSelectFlower(view: ConstraintLayout, isSelected: Boolean) {
    view.backgroundTintList = if (isSelected) { // 선택되었을 때
        ContextCompat.getColorStateList(view.context, R.color.gray)
    } else { // 선택되지 않았을 때
        ContextCompat.getColorStateList(view.context, R.color.white)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setUsersQuestSize")
fun setUsersQuestSize(view: TextView, size: Int) {
    view.text = "$size / 2"
}

@BindingAdapter("app:setStateIcon")
fun setStateIcon(view: ImageView, state: Int) {
    if (state != 0) {
        val imageRes = when (state) {
            UsersQuest.STATE_NOTHING -> R.drawable.ic_active_quest
            UsersQuest.STATE_LOADING -> R.drawable.ic_non_active_quest
            else -> R.drawable.ic_complete_quest
        }

        Glide.with(view.context)
            .load(imageRes)
            .into(view)
    }
}

@BindingAdapter("app:setStateLayout")
fun setStateLayout(view: LinearLayout, state: Int) {
    if (state != 0) {
        val layoutColor = when (state) {
            UsersQuest.STATE_NOTHING -> R.color.yellow
            UsersQuest.STATE_LOADING -> R.color.gray
            else -> R.color.green_300
        }

        view.backgroundTintList = ContextCompat.getColorStateList(
            view.context,
            layoutColor
        )
    }
}

@BindingAdapter("app:setStateText")
fun setStateText(view: TextView, state: Int) {
    if (state != 0) {
        val stateText = when (state) {
            UsersQuest.STATE_NOTHING -> "수행하기"
            UsersQuest.STATE_LOADING -> "인증 중"
            UsersQuest.STATE_COMPLETE_WITH_REWARD -> "보상받기"
            UsersQuest.STATE_COMPLETE -> "완료"
            else -> ""
        }

        view.text = stateText
    }
}

@BindingAdapter("app:setStateButton", "app:setQuestSizeButton", requireAll = true)
fun setStateButton(view: Button, state: Int, usersQuestSize: Int) {
    if (state != 0) { // 내 퀘스트를 클릭하였을 경우
        when (state) {
            UsersQuest.STATE_NOTHING -> view.visibility = View.GONE // 퀘스트 수락 후
            UsersQuest.STATE_LOADING -> { // 퀘스트 수행 후
                with(view) {
                    visibility = View.VISIBLE
                    isEnabled = false
                    text = "보상받기"

                    backgroundTintList = ContextCompat.getColorStateList(
                        view.context,
                        R.color.gray
                    )
                }
            }
            UsersQuest.STATE_COMPLETE_WITH_REWARD -> { // 퀘스트를 다른 유저에게 인증을 받은 후
                with(view) {
                    visibility = View.VISIBLE
                    isEnabled = true
                    text = "보상받기"

                    backgroundTintList = ContextCompat.getColorStateList(
                        view.context,
                        R.color.green_300
                    )
                }
            }
            UsersQuest.STATE_COMPLETE -> { // 보상 받은 후
                with(view) {
                    visibility = View.VISIBLE
                    isEnabled = false
                    text = "완료"

                    backgroundTintList = ContextCompat.getColorStateList(
                        view.context,
                        R.color.green_300
                    )
                }
            }
        }
    } else { // 퀘스트 목록에 있는것을 클릭하였을 경우
        if (usersQuestSize != 2) { // 유저가 2개 이하로 퀘스트를 가지고 있을 경우
            with(view) {
                visibility = View.VISIBLE
                isEnabled = true
                text = "수락하기"

                backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.green_300
                )
            }
        } else { // 유저가 이미 2개의 퀘스트를 가지고 있을 경우
            with(view) {
                visibility = View.VISIBLE
                isEnabled = false
                text = "수락하기"

                backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.gray
                )
            }
        }
    }
}

@BindingAdapter("setButtonLoading")
fun setButtonLoading(view: Button, isLoading: Boolean) {
    if (isLoading) {
        view.isEnabled = false
        view.text = ""
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setPostTimestamp")
fun setPostTimestamp(view: TextView, timeStamp: Long) {
    val diffTime = System.currentTimeMillis() - timeStamp
    view.text = when {
        diffTime / 3600000 >= 24 -> {
            SimpleDateFormat("yy.MM.dd", Locale.KOREA).format(timeStamp)
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
}

@BindingAdapter("app:setSelectedText")
fun setSelectedText(view: TextView, isSelected: Boolean) {
    if (isSelected) {
        view.setTextColor(view.resources.getColor(R.color.default_item_color, null))
    } else {
        view.setTextColor(view.resources.getColor(android.R.color.tab_indicator_text, null))
    }
}

@BindingAdapter("app:setDiaryTimestamp")
fun setDiaryTimestamp(view: TextView, timeStamp: Long) {
    view.text = SimpleDateFormat("yy.MM.dd (EE)", Locale.KOREA).format(timeStamp)
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
//    view.text = "Lv: ${user.myLevel} | 수행한 미션: ${user.achievedCount}"
}

@BindingAdapter("app:errorMessage")
fun errorMessage(view: TextView, errorMsg: String) {
    if (errorMsg.isNotEmpty() && errorMsg != ErrorMessage.SUCCESS) {
        view.visibility = View.VISIBLE
        view.text = errorMsg
    }
}

@BindingAdapter("app:setRefresh")
fun setRefresh(view: SwipeRefreshLayout, isLoading: Boolean) {
    view.isRefreshing = isLoading
}

@BindingAdapter("app:setNativeAd")
fun setNativeAd(view: NativeAdView, nativeAd: NativeAd?) {
    if (nativeAd != null) {
        view.setNativeAd(nativeAd)
    }
}

@BindingAdapter("app:setMediaContents")
fun setMediaContents(view: MediaView, content: MediaContent?) {
    if (content != null) {
        view.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        view.setMediaContent(content)
    }
}

@BindingAdapter("app:setImage", "app:setUriImage", "app:setResourceImage", requireAll = false)
fun setImage(view: ImageView, image: String?, uri: Uri?, res: Int?) {
    view.clipToOutline = true

    if (image != null) {
        val imageByte = Base64.decode(image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(
            imageByte,
            0,
            imageByte.size
        )

        Glide.with(view.context)
            .load(bitmap)
            .into(view)
    } else if (uri != null) {
        Glide.with(view.context)
            .load(uri)
            .into(view)
    } else if (res != null) {
        Glide.with(view.context)
            .load(res)
            .into(view)
    }
}

@BindingAdapter("app:isFavorite")
fun isFavorite(view: ImageView, favorite: Boolean) {
    if (favorite) {
        view.setImageResource(R.drawable.ic_thumb_up)
    } else {
        view.setImageResource(R.drawable.ic_thumb_up_off)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setPosition", "app:setSize", requireAll = true)
fun setPosition(view: TextView, pos: Int, size: Int) {
    view.text = "${pos}/${size}"
}

@BindingAdapter("app:setProfileImage")
fun setProfileImage(view: CircleImageView, imageUri: Uri?) {
    if (imageUri != null) {
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

@BindingAdapter("app:setCheckBox")
fun setCheckBox(view: CheckBox, isChecked: Boolean) {
    view.isChecked = isChecked
}

// Listener
@BindingAdapter("app:setRefreshListener")
fun setRefreshListener(view: SwipeRefreshLayout, listener: InverseBindingListener) {
    view.setOnRefreshListener {
        listener.onChange()
    }
}

@BindingAdapter("app:setCheckBoxListener")
fun setCheckBoxListener(view: CheckBox, listener: InverseBindingListener) {
    view.setOnCheckedChangeListener { buttonView, isChecked ->
        listener.onChange()
    }
}

// Get
@InverseBindingAdapter(attribute = "app:setRefresh", event = "app:setRefreshListener")
fun getRefresh(view: SwipeRefreshLayout): Boolean {
    return view.isRefreshing
}

@InverseBindingAdapter(attribute = "app:setCheckBox", event = "app:setCheckBoxListener")
fun getCheckBox(view: CheckBox): Boolean {
    return view.isChecked
}