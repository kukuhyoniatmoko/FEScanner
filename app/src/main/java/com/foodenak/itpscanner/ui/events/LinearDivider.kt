package com.foodenak.itpscanner.ui.events

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.View
import com.foodenak.itpscanner.utils.getDrawacleCompat

/**
 * Created by ITP on 5/5/2015.
 */
class LinearDivider
@SuppressWarnings("deprecation")
constructor(context: Context, @DrawableRes drawableRes: Int) : RecyclerView.ItemDecoration() {

    protected var mDivider: Drawable

    init {
        val resources = context.resources
        mDivider = resources.getDrawacleCompat(drawableRes)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val first = parent.layoutManager.findViewByPosition(0)
        if (first === view) {
            outRect.top = mDivider.intrinsicHeight
        }
        outRect.bottom = mDivider.intrinsicHeight
        outRect.left = mDivider.intrinsicHeight
        outRect.right = mDivider.intrinsicHeight
    }
}
