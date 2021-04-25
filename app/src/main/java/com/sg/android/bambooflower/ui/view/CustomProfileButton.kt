package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sg.android.bambooflower.R

class CustomProfileButton : ConstraintLayout {
    private lateinit var buttonIconView: ImageView
    private lateinit var buttonTextView: TextView

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
        val view = inflater.inflate(R.layout.custom_profile_button, this, false)
        addView(view)

        buttonIconView = findViewById(R.id.button_icon)
        buttonTextView = findViewById(R.id.button_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProfileButton)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomProfileButton,
                defStyleAttr,
                0
            )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val buttonIcon =
            typedArray.getResourceId(R.styleable.CustomProfileButton_profileButtonIcon, 0)
        val buttonText =
            typedArray.getString(R.styleable.CustomProfileButton_profileButtonText)

        buttonIconView.setImageResource(buttonIcon)
        buttonTextView.text = buttonText
        typedArray.recycle()
    }
}