package com.foodenak.itpscanner.services.image

import android.widget.ImageView

/**
 * Created by ITP on 10/8/2015.
 */
interface ImageLoader {

    fun load(uri: String, imageView: ImageView)
}
