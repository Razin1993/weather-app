package com.razin.weather.ui.uiUtils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BottomPaddingForRecycler(private val padding:Int):RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State){
        super.getItemOffsets(outRect, view, parent, state)
        val itemCount = parent.adapter?.itemCount ?: 0
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == itemCount - 1) {
            outRect.bottom = padding
        }
    }
}