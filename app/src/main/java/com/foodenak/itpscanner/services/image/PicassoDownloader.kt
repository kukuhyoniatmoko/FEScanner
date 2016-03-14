package com.foodenak.itpscanner.services.image

import android.content.Context
import android.net.Uri
import com.squareup.picasso.Downloader
import com.squareup.picasso.NetworkPolicy
import dagger.Lazy
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class PicassoDownloader @Inject constructor(val context: Context, val provider: Lazy<OkHttpClient>) : Downloader {

    @Throws(IOException::class)
    override fun load(uri: Uri, networkPolicy: Int): Downloader.Response {
        var cacheControl: CacheControl? = null
        if (networkPolicy != 0) {
            if (NetworkPolicy.isOfflineOnly(networkPolicy)) {
                cacheControl = CacheControl.FORCE_CACHE
            } else {
                val builder = CacheControl.Builder()
                if (!NetworkPolicy.shouldReadFromDiskCache(networkPolicy)) {
                    builder.noCache()
                }
                if (!NetworkPolicy.shouldWriteToDiskCache(networkPolicy)) {
                    builder.noStore()
                }
                cacheControl = builder.build()
            }
        }

        val builder = Request.Builder().url(uri.toString())
        if (cacheControl != null) {
            builder.cacheControl(cacheControl)
        }

        val response = provider.get().newCall(builder.build()).execute()
        val responseCode = response.code()
        if (responseCode >= 300) {
            response.body().close()
            throw Downloader.ResponseException(responseCode.toString() + " " + response.message(), networkPolicy,
                    responseCode)
        }

        val fromCache = response.cacheResponse() != null

        val responseBody = response.body()
        return Downloader.Response(responseBody.byteStream(), fromCache, responseBody.contentLength())
    }

    override fun shutdown() {
        //this instance does not own the client
    }
}