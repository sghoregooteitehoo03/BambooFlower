package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.sg.android.bambooflower.R

class CustomButton : LinearLayout {
    private lateinit var buttonLayout: LinearLayout
    private lateinit var buttonImageView: ImageView
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
        val view = inflater.inflate(R.layout.custom_button, this, false)
        addView(view)

        buttonLayout = findViewById(R.id.button_layout)
        buttonImageView = findViewById(R.id.button_icon)
        buttonTextView = findViewById(R.id.button_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomButton, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val buttonIcon = typedArray.getResourceId(R.styleable.CustomButton_customButtonIcon, 0)
        val buttonText = typedArray.getString(R.styleable.CustomButton_customButtonText)

        buttonImageView.setImageResource(buttonIcon)
        buttonTextView.text = buttonText
        typedArray.recycle()
    }

    fun setCustomEnabled(isEnabled: Boolean) {
        buttonLayout.isEnabled = isEnabled
        setEnabled(isEnabled)
    }
}