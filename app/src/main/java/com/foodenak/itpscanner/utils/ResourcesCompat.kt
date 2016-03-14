package com.foodenak.itpscanner.utils

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DrawableRes

/**
 * Created by kukuh on 15/11/16.
 */
@Suppress("Deprecation") fun Resources.getDrawacleCompat(@DrawableRes drawableRes: Int): Drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
    getDrawable(drawableRes, null)
} else {
    getDrawable(drawableRes)
}