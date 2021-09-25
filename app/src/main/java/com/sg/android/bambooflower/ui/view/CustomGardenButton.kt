package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sg.android.bambooflower.R

class CustomGardenButton : ConstraintLayout {
    private lateinit var mainLayout: LinearLayout
    private lateinit var iconImageView: ImageView

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
        val view = inflater.inflate(R.layout.custom_garden_button, this, false)
        addView(view)

        mainLayout = findViewById(R.id.main_layout)
        iconImageView = findViewById(R.id.icon_image)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomGardenButton)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomGardenButton,
                defStyleAttr,
                0
            )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val iconImage = typedArray.getResourceId(R.styleable.CustomGardenButton_customGardenIcon, 0)
        val isSelect = typedArray.getBoolean(R.styleable.CustomGardenButton_customGardenSelected, false)

        mainLayout.backgroundTintList = if (isSelect) {
            ContextCompat.getColorStateList(context, R.color.inventory_active_color)
        } else {
            ContextCompat.getColorStateList(context, R.color.inventory_non_active_color)
        }
        iconImageView.setImageResource(iconImage)
        typedArray.recycle()
    }

    fun setIconImage(_imageRes: Int) {
        iconImageView.setImageResource(_imageRes)
    }

    fun setSelect(_isSelect: Boolean) {
        mainLayout.backgroundTintList = if (_isSelect) {
            ContextCompat.getColorStateList(context, R.color.inventory_active_color)
        } else {
            ContextCompat.getColorStateList(context, R.color.inventory_non_active_color)
        }
    }
}