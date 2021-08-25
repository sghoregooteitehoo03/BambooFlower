package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.sg.android.bambooflower.R

class CustomProgressView : ConstraintLayout {
    private lateinit var customProgressView: ProgressBar
    private lateinit var customProgressTextView: TextView

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
        val view = inflater.inflate(R.layout.custom_progress_view, this, false)
        addView(view)

        customProgressView = findViewById(R.id.custom_progress)
        customProgressTextView = findViewById(R.id.custom_progress_text)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgressView)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.CustomProgressView,
                defStyleAttr,
                0
            )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val progress = typedArray.getInt(R.styleable.CustomProgressView_customSetProgress, 0)
        val secondProgress = typedArray.getInt(R.styleable.CustomProgressView_customSetSecondProgress, 0)
        val progressText = typedArray.getString(R.styleable.CustomProgressView_customSetProgressText)

        customProgressView.progress = progress
        customProgressView.secondaryProgress = secondProgress
        customProgressTextView.text = progressText
        typedArray.recycle()
    }

    fun setProgress(progress: Int) {
        customProgressView.progress = progress
    }

    fun setSecondProgress(progress: Int) {
        customProgressView.secondaryProgress = progress
    }

    fun setProgressText(text: String) {
        customProgressTextView.text = text
    }

    fun getProgressView() =
        customProgressView
}