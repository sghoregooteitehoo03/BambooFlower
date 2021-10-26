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
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.textfield.TextInputEditText
import com.sg.android.bambooflower.R
import com.sg.android.bambooflower.data.Shop
import com.sg.android.bambooflower.data.User
import com.sg.android.bambooflower.data.UsersQuest
import com.sg.android.bambooflower.ui.view.CustomGardenButton
import com.sg.android.bambooflower.ui.view.CustomProgressView
import com.sg.android.bambooflower.ui.view.CustomRewardButton
import com.sg.android.bambooflower.ui.view.CustomSettingView
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

@BindingAdapter("app:setOrdinaryProgress", "app:setNewProgress", requireAll = true)
fun setFlowerProgress(view: CustomProgressView, ordinaryProgress: Int, newProgress: Int) {
    if (newProgress != -1) {
        with(view) {
            setProgress(ordinaryProgress)
            setSecondProgress(ordinaryProgress + newProgress)
            setProgressText("+$newProgress")
        }
    }
}

@BindingAdapter("app:setTitleAction")
fun setTitleAction(view: TextView, isQuest: Boolean) {
    view.text = if (isQuest) {
        "퀘스트 수행 완료!"
    } else {
        "하루일기 작성 완료!"
    }
}

@BindingAdapter("app:setFlowerButtonEnable")
fun setFlowerButtonEnable(view: Button, isEnable: Boolean) {
    if (isEnable) { // 버튼 활성화
        view.isEnabled = true
        view.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.green_300)

        view.text = "선택"
    } else { // 버튼 비활성화
        view.isEnabled = false
        view.text = "선택"

        view.backgroundTintList =
            ContextCompat.getColorStateList(view.context, R.color.gray_night_color)
    }
}

@BindingAdapter("app:setSelectFlower")
fun setSelectFlower(view: ConstraintLayout, isSelected: Boolean) {
    view.backgroundTintList = if (isSelected) { // 선택되었을 때
        ContextCompat.getColorStateList(view.context, R.color.gray_night_color)
    } else { // 선택되지 않았을 때
        ContextCompat.getColorStateList(view.context, R.color.default_dialog_bg_color)
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setUsersQuestSize")
fun setUsersQuestSize(view: TextView, size: Int) {
    view.text = "$size / 2"
}

@BindingAdapter("app:setLoadingListLayout", "app:setInvisible", requireAll = false)
fun setLoadingListLayout(view: ShimmerFrameLayout, isLoading: Boolean, isInvisible: Boolean?) {
    if (isLoading) {
        view.visibility = View.VISIBLE
        view.startShimmer()
    } else {
        view.visibility = if (isInvisible == true) {
            View.INVISIBLE
        } else {
            View.GONE
        }
        view.stopShimmer()
    }
}

@BindingAdapter("app:setStateIcon")
fun setStateIcon(view: ImageView, state: Int) {
    if (state != 0) {
        val imageRes = if (state != UsersQuest.STATE_COMPLETE) {
            R.drawable.ic_close_chest
        } else {
            R.drawable.ic_open_chest
        }

        Glide.with(view.context)
            .load(imageRes)
            .into(view)
    }
}

@BindingAdapter("app:setStateText")
fun setStateText(view: TextView, state: Int) {
    if (state != 0) {
        when (state) {
            UsersQuest.STATE_NOTHING -> {
                view.text = "수행하기"
                view.backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.yellow
                )
            }
            UsersQuest.STATE_LOADING -> {
                view.text = "인증 중"
                view.backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.gray_night_color
                )
            }
            UsersQuest.STATE_COMPLETE_WITH_REWARD -> {
                view.text = "보상받기"
                view.backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.green_300
                )
            }
            UsersQuest.STATE_COMPLETE -> {
                view.text = "완료"
                view.backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.green_300
                )
            }
        }
    }
}

@BindingAdapter("app:setStateButton")
fun setStateButton(view: Button, state: Int) {
    when (state) {
        UsersQuest.STATE_NOTHING -> view.visibility = View.GONE // 퀘스트 수락 후
        UsersQuest.STATE_LOADING -> { // 퀘스트 수행 후
            with(view) {
                visibility = View.VISIBLE
                isEnabled = false
                text = "보상받기"

                backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.gray_night_color
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
}

@SuppressLint("SetTextI18n")
@BindingAdapter("app:setQuestSize", "app:setQuestExists", requireAll = true)
fun setQuestButton(view: Button, questSize: Int, questExists: Boolean) {
    if (questExists) { // 이미 수락한 퀘스트일 경우
        with(view) {
            isEnabled = false
            text = "이미 수락한 퀘스트입니다"

            backgroundTintList = ContextCompat.getColorStateList(
                view.context,
                R.color.gray_night_color
            )
        }
    } else {
        with(view) {
            text = "수락하기 ${questSize}/2"

            if (questSize < 2) { // 퀘스트 제한량을 넘지 않았을 경우
                isEnabled = true
                backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.green_300
                )
            } else {
                isEnabled = false
                backgroundTintList = ContextCompat.getColorStateList(
                    view.context,
                    R.color.gray_night_color
                )
            }
        }
    }
}

@BindingAdapter("app:setButtonLoading")
fun setButtonLoading(view: Button, isLoading: Boolean) {
    if (isLoading) {
        view.isEnabled = false
        view.text = ""
    }
}

@BindingAdapter("app:enableRewardButton")
fun enableRewardButton(view: CustomRewardButton, isEnable: Boolean) {
    view.isEnabled = isEnable
    if (isEnable) {
        view.setBackgroundTint(
            ContextCompat.getColorStateList(
                view.context,
                R.color.green_300
            )!!
        )
    } else {
        view.setBackgroundTint(
            ContextCompat.getColorStateList(
                view.context,
                R.color.gray_night_color
            )!!
        )
    }
}

@BindingAdapter("app:setEnableDefaultButton")
fun setEnableDefaultButton(view: Button, isEnable: Boolean) {
    view.isEnabled = isEnable

    if (isEnable) {
        view.backgroundTintList = ContextCompat.getColorStateList(
            view.context,
            R.color.green_300
        )!!
    } else {
        view.backgroundTintList = ContextCompat.getColorStateList(
            view.context,
            R.color.gray_night_color
        )!!
    }
}

@BindingAdapter("app:setPointButton")
fun setPointButton(view: CustomRewardButton, point: Int) {
    view.setPointText("+$point")
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

@BindingAdapter("app:setGrowCountText")
fun setGrowCountText(view: TextView, count: Int) {
    view.text = "성장시킨 횟수: $count"
}

@BindingAdapter("app:setPagerText")
fun setPagerText(view: TextView, isSelected: Boolean) {
    if (isSelected) {
        view.setTextColor(view.resources.getColor(R.color.default_item_color, null))
    } else {
        view.setTextColor(view.resources.getColor(android.R.color.tab_indicator_text, null))
    }
}

@BindingAdapter("app:setShopPrice")
fun setShopPrice(view: TextView, shop: Shop) {
    view.text = if (shop.isExists) {
        "구매 완료"
    } else {
        if (shop.price == 0) {
            "광고 보기"
        } else {
            shop.price.toString()
        }
    }
}

@BindingAdapter("app:cardExistsColor")
fun cardExistsColor(view: CardView, isExists: Boolean) {
    if (isExists) {
        view.setCardBackgroundColor(
            view.resources.getColor(R.color.divide_color, null)
        )
    } else {
        view.setCardBackgroundColor(
            view.resources.getColor(R.color.orange_bright, null)
        )
    }
}

@BindingAdapter("app:setExistsColor")
fun setExistsColor(view: LinearLayout, isExists: Boolean) {
    if (isExists) {
        view.backgroundTintList = ContextCompat.getColorStateList(
            view.context,
            R.color.gray_night_color
        )
    } else {
        view.backgroundTintList = ContextCompat.getColorStateList(
            view.context,
            R.color.deep_orange_300
        )
    }
}

@BindingAdapter("app:setSettingSwitch")
fun setSettingSwitch(view: CustomSettingView, isSwitched: Boolean) {
    view.setSwitchView(isSwitched)
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

@BindingAdapter("app:setProfileImage")
fun setProfileImage(view: CircleImageView, image: String?) {
    if (image != null) {
        if (image.isNotEmpty()) {
            val imageByte = Base64.decode(image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(
                imageByte,
                0,
                imageByte.size
            )

            Glide.with(view.context)
                .load(bitmap)
                .into(view)
        } else {
            Glide.with(view.context)
                .load(R.drawable.ic_person)
                .into(view)
        }
    }
}

@BindingAdapter("app:isCheer")
fun isCheer(view: ImageView, cheer: Boolean) {
    if (cheer) {
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

@BindingAdapter("app:setExpandIcon")
fun setExpandIcon(view: CustomGardenButton, isExpand: Boolean) {
    if (isExpand) {
        view.setIconImage(R.drawable.ic_collapse)
    } else {
        view.setIconImage(R.drawable.ic_expand)
    }
}

@BindingAdapter("app:setExpandGrayIcon")
fun setExpandGrayIcon(view: ImageView, isExpand: Boolean) {
    if (isExpand) {
        view.setImageResource(R.drawable.ic_collapse_gray)
    } else {
        view.setImageResource(R.drawable.ic_expand_gray)
    }
}

@BindingAdapter("app:setInventoryFilter")
fun setInventoryFilter(view: CustomGardenButton, isSelected: Boolean) {
    view.setSelect(isSelected)
}

@BindingAdapter("app:setShowing", "app:setCollocated", requireAll = true)
fun setTileView(view: View, isShowing: Boolean, isCollocated: Boolean) {
    if (isShowing) {
        view.visibility = View.VISIBLE

        if (isCollocated) {
            view.setBackgroundResource(R.drawable.shape_non_able_collocate)
        } else {
            view.setBackgroundResource(R.drawable.shape_able_collocate)
        }
    } else {
        view.visibility = View.GONE
    }
}

@BindingAdapter("app:setEdited")
fun setEdited(view: TextView, isEdited: Boolean) {
    val color = if (isEdited) {
        view.resources.getColor(R.color.black, null)
    } else {
        view.resources.getColor(android.R.color.tab_indicator_text, null)
    }

    view.isEnabled = isEdited
    view.setTextColor(color)
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