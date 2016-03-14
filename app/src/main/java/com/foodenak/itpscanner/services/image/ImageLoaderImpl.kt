package com.foodenak.itpscanner.services.image

import android.widget.ImageView
import com.squareup.picasso.Picasso

/**
 * Created by ITP on 10/8/2015.
 */
class ImageLoaderImpl(val picasso: Picasso) : ImageLoader {

    override fun load(uri: String, imageView: ImageView) {
        picasso.load(uri).into(imageView)
    }
}