package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sg.android.bambooflower.R

class CustomSettingView : ConstraintLayout {
    private lateinit var customTextView: TextView
    private lateinit var customIconView: ImageView

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView()
        getAttrs(attrs, defStyle)
    }

    private fun initView() {
        val inflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_setting_view, this, false)
        addView(view)

        customTextView = findViewById(R.id.custom_text)
        customIconView = findViewById(R.id.custom_image)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomSettingView)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomSettingView,
                defStyleAttr,
                0
            )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val text =
            typedArray.getString(R.styleable.CustomSettingView_settingText)
        val textColor = typedArray.getColor(
            R.styleable.CustomSettingView_settingTextColor,
            resources.getColor(R.color.black, null)
        )
        val visible =
            typedArray.getBoolean(R.styleable.CustomSettingView_settingIconVisible, true)

        customTextView.text = text
        customTextView.setTextColor(textColor)
        customIconView.visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        typedArray.recycle()
    }

    fun setSettingText(text: String) {
        customTextView.text = text
    }
}