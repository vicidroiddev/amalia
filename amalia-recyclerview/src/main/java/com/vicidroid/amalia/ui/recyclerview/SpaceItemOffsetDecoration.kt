package com.vicidroid.amalia.ui.recyclerview

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemOffsetDecoration(
    context: Context,
    spaceInDp: Int) : RecyclerView.ItemDecoration() {

    private val spaceInPixels =
        Math.round(spaceInDp * context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = spaceInPixels
        outRect.right = spaceInPixels
        outRect.bottom = spaceInPixels

        // Only first view should have a top offset, otherwise it is double counted with bottom offset
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = spaceInPixels
        }
    }
}