package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.sg.android.bambooflower.R

class CustomMissionButton : LinearLayout {
    private lateinit var missionLayout: LinearLayout
    private lateinit var missionIcon: ImageView
    private lateinit var missionText: TextView

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
        val view = inflater.inflate(R.layout.custom_mission_button, this, false)
        addView(view)

        missionLayout = findViewById(R.id.mission_layout)
        missionIcon = findViewById(R.id.mission_image)
        missionText = findViewById(R.id.mission_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMissionButton)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CustomMissionButton, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val buttonIcon = typedArray.getResourceId(R.styleable.CustomMissionButton_missionButtonIcon, 0)
        val buttonText = typedArray.getString(R.styleable.CustomMissionButton_missionButtonText)
        val buttonTextColor =
            typedArray.getColor(R.styleable.CustomMissionButton_missionButtonTextColor, 0)

        missionIcon.setImageResource(buttonIcon)
        missionText.text = buttonText
        missionText.setTextColor(buttonTextColor)
        typedArray.recycle()
    }

    fun setButtonText(_text: String, _color: Int) {
        missionText.text = _text
        missionText.setTextColor(_color)
    }

    fun setButtonIcon(_resource: Int) {
        missionIcon.setImageResource(_resource)
    }
}