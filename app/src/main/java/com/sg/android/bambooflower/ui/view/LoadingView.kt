package com.sg.android.bambooflower.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.sg.android.bambooflower.R

class LoadingView : LinearLayout {
    private lateinit var loadingLayout: LinearLayout

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
        val view = inflater.inflate(R.layout.custom_loading_view, this, false)
        addView(view)

        loadingLayout = findViewById(R.id.loading_view_layout)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        loadingLayout.visibility =
            if (typedArray.getBoolean(R.styleable.LoadingView_visible, false)) {
                View.VISIBLE
            } else {
                View.GONE
            }

        typedArray.recycle()
    }

    @SuppressLint("InlinedApi")
    fun setVisible(isVisible: Boolean, window: Window) {
        loadingLayout.visibility = if (isVisible) {
            with(window) {
                decorView.systemUiVisibility = 0
                statusBarColor = Color.BLACK
            }

            View.VISIBLE
        } else {
            with(window) {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        decorView.systemUiVisibility = 0
                        statusBarColor = Color.BLACK
                    }
                    Configuration.UI_MODE_NIGHT_NO -> {
                        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        statusBarColor = Color.WHITE
                    }
                }
            }

            View.GONE
        }
    }
}