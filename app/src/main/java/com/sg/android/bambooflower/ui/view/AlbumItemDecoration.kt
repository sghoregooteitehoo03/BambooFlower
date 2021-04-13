package com.sg.android.bambooflower.ui.view

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AlbumItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private var size10: Int = 0
    private var size5: Int = 0

    init {
        size10 = dpToPx(context, 10f)
        size5 = dpToPx(context, 5f)
    }

    private fun dpToPx(context: Context, dp: Float) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        // 상하 설정
        if (position == 0 || position == 1) {
            outRect.top = size10
            outRect.bottom = size10
        } else {
            outRect.bottom = size10
        }

        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex

        // 왼쪽 아이템
        if (spanIndex == 0) {
            outRect.left = size10
            outRect.right = size5
        }
        // 오른쪽 아이템
        else if (spanIndex == 1) {
            outRect.left = size5
            outRect.right = size10
        }
    }
}