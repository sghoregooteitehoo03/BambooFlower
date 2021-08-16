package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.sg.android.bambooflower.R

class CustomMenuView : ConstraintLayout {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var subLayout: LinearLayout
    private lateinit var menuImageView: ImageView
    private lateinit var menuTitleView: TextView

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
        val view = inflater.inflate(R.layout.custom_menu_view, this, false)
        addView(view)

        mainLayout = findViewById(R.id.menu_main_layout)
        subLayout = findViewById(R.id.menu_sub_layout)
        menuImageView = findViewById(R.id.menu_image)
        menuTitleView = findViewById(R.id.menu_title_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomMenuView)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomMenuView,
                defStyleAttr,
                0
            )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val mainColor = typedArray.getResourceId(R.styleable.CustomMenuView_customMenuMainColor, 0)
        val subColor = typedArray.getResourceId(R.styleable.CustomMenuView_customMenuSubColor, 0)
        val menuImage = typedArray.getResourceId(R.styleable.CustomMenuView_customMenuImage, 0)
        val menuTitle = typedArray.getString(R.styleable.CustomMenuView_customMenuTitle)

        mainLayout.backgroundTintList = ContextCompat.getColorStateList(context, mainColor)
        subLayout.backgroundTintList = ContextCompat.getColorStateList(context, subColor)
        menuImageView.setImageResource(menuImage)
        menuTitleView.text = menuTitle
        typedArray.recycle()
    }
}