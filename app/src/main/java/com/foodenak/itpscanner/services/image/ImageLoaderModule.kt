package com.foodenak.itpscanner.services.image

import android.content.Context
import com.foodenak.itpscanner.services.ClientModule
import com.squareup.picasso.Picasso
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Module
class ImageLoaderModule {

    @Provides
    @Singleton
    internal fun provideImageLoader(context: Context, @Named(ClientModule.IMAGE_CLIENT_PROVIDER) provider: Lazy<OkHttpClient>): ImageLoader {
        return ImageLoaderImpl(Picasso.Builder(context)
                .downloader(PicassoDownloader(context, provider))
                .build())
    }
}