package com.foodenak.itpscanner.ui.events

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by ITP on 7/29/2015.
 */
class GridDivider(private val mSpanCount: Int, private val mSpacing: Int, private val mIncludeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % mSpanCount // item column

        if (mIncludeEdge) {
            outRect.left = mSpacing - column * mSpacing / mSpanCount // mSpacing - column * ((1f / mSpanCount) * mSpacing)
            outRect.right = (column + 1) * mSpacing / mSpanCount // (column + 1) * ((1f / mSpanCount) * mSpacing)

            if (position < mSpanCount) {
                // top edge
                outRect.top = mSpacing
            }
            outRect.bottom = mSpacing // item bottom
        } else {
            outRect.left = column * mSpacing / mSpanCount // column * ((1f / mSpanCount) * mSpacing)
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount // mSpacing - (column + 1) * ((1f /    mSpanCount) * mSpacing)
            //            if (position >= mSpanCount) {
            //                outRect.top = mSpacing; // item top
            //            }
        }
    }
}
