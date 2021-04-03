package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sg.android.bambooflower.R

class CustomLoginButton : LinearLayout {
    private lateinit var loginLayout: LinearLayout
    private lateinit var loginIcon: ImageView
    private lateinit var loginText: TextView

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
        val view = inflater.inflate(R.layout.custom_login_button, this, false)
        addView(view)

        loginLayout = findViewById(R.id.login_layout)
        loginIcon = findViewById(R.id.login_icon)
        loginText = findViewById(R.id.login_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLoginButton)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomLoginButton, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val buttonIcon = typedArray.getResourceId(R.styleable.CustomLoginButton_buttonIcon, 0)
        val buttonText = typedArray.getString(R.styleable.CustomLoginButton_buttonText)
        val buttonTextColor =
            typedArray.getColor(R.styleable.CustomLoginButton_buttonTextColor, 0)

        loginIcon.setImageResource(buttonIcon)
        loginText.text = buttonText
        loginText.setTextColor(buttonTextColor)
        typedArray.recycle()
    }
}