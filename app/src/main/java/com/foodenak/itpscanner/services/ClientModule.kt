package com.foodenak.itpscanner.services

import android.content.Context
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import android.util.Log
import com.foodenak.itpscanner.BuildConfig
import com.foodenak.itpscanner.R
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager

/**
 * Created by ITP on 10/5/2015.
 */
@Module class ClientModule {

    @Provides @Singleton @Named(POOL_CLIENT_PROVIDER)
    internal fun providePoolClientProvider(@Named(DEFAULT_CLIENT_PROVIDER) okHttpClient: OkHttpClient): OkHttpClient {
        val builder = okHttpClient.newBuilder()
        builder.connectTimeout(10, TimeUnit.MINUTES)
        builder.readTimeout(10, TimeUnit.MINUTES)
        builder.writeTimeout(10, TimeUnit.MINUTES)
        return builder.build()
    }

    @Provides @Singleton @Named(DEFAULT_CLIENT_PROVIDER)
    internal fun provideDefaultClientProvider(okHttpClient: OkHttpClient, interceptor: FERequestInterceptor): OkHttpClient {
        val builder = okHttpClient.newBuilder()
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.addNetworkInterceptor(interceptor)
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor.Logger { message ->
                val max = 1500
                if (TextUtils.isEmpty(message) || message.length < 1500) {
                    Log.i(OK_HTTP, message)
                    return@Logger
                }
                var start = 0
                val length = message.length
                while (start < length) {
                    Log.i(OK_HTTP, message.substring(start, start + Math.min(max, length - start)))
                    start += max
                }
            }
            val loggingInterceptor = HttpLoggingInterceptor(logger)
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addNetworkInterceptor(loggingInterceptor)
        }
        return builder.build()
    }

    @Provides @Singleton @Named(IMAGE_CLIENT_PROVIDER)
    internal fun provideImageClientProvider(okHttpClient: OkHttpClient): OkHttpClient {
        val builder = okHttpClient.newBuilder()
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.connectTimeout(30, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor.Logger { message -> Log.i(OK_HTTP, message) }
            val interceptor = HttpLoggingInterceptor(logger)
            interceptor.level = HttpLoggingInterceptor.Level.HEADERS
            builder.addNetworkInterceptor(interceptor)
        }
        builder.addNetworkInterceptor { chain ->
            val response = chain.proceed(chain.request())
            if (response.cacheResponse() != null) return@addNetworkInterceptor response
            val cacheHeader = response.header("Cache-Control")
            if (!TextUtils.isEmpty(cacheHeader)) return@addNetworkInterceptor response
            response.newBuilder().addHeader("Cache-Control", "max-age = 86400").build()
        }
        return builder.build()
    }

    @Provides @Singleton internal fun provideOkHttpClient(context: Context): OkHttpClient {
        return initClientInternal(context)
    }

    private val TAG: String = "ClientModule";

    private fun initClientInternal(context: Context): OkHttpClient {
        val builder = OkHttpClient.Builder()
        try {
            val r = context.resources

            val fe = createCertificate(r.openRawResource(R.raw.fe))

            val feAws = createCertificate(r.openRawResource(R.raw.feaws))

            val keyStoreType = KeyStore.getDefaultType()
            val keyStore = KeyStore.getInstance(keyStoreType)
            keyStore.load(null, null)
            keyStore.setCertificateEntry("fe", fe)
            keyStore.setCertificateEntry("feAws", feAws)

            val ctm = CustomTrustManagers(keyStore)

            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, arrayOf<TrustManager>(ctm), null)
            builder.sslSocketFactory(sslContext.socketFactory)
        } catch (e: Exception) {
            Log.e(TAG, "Fail to set SSL Socket Factory of okHttpClient", e)
        }
        val cache = File(context.applicationContext.cacheDir, "picasso-cache")
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs()
        }
        builder.cache(Cache(cache, calculateDiskCacheSize(cache)))
        return builder.build()
    }

    fun calculateDiskCacheSize(dir: File): Long {
        var size = MIN_DISK_CACHE_SIZE.toLong()

        try {
            val statFs = StatFs(dir.absolutePath)
            val available = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                (statFs.blockCountLong) * statFs.blockSizeLong
            } else {
                (statFs.blockCount.toLong()) * statFs.blockSize.toLong()
            }
            // Target 2% of the total space.
            size = available / 50
        } catch (ignored: IllegalArgumentException) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE.toLong()), MIN_DISK_CACHE_SIZE.toLong())
    }

    @Throws(CertificateException::class)
    private fun createCertificate(`in`: InputStream): Certificate {
        val cf = CertificateFactory.getInstance("X.509")

        val certificate: Certificate
        var stream: BufferedInputStream? = null
        try {
            stream = BufferedInputStream(`in`)
            certificate = cf.generateCertificate(stream)
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: IOException) {
                    // ignore
                }

            }
        }
        return certificate
    }

    companion object {

        const val IMAGE_CLIENT_PROVIDER = "IMAGE_CLIENT_PROVIDER"
        const val DEFAULT_CLIENT_PROVIDER = "DEFAULT_CLIENT_PROVIDER"
        const val POOL_CLIENT_PROVIDER = "POOL_CLIENT_PROVIDER"
        const val OK_HTTP = "OkHttp";
        const val MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024 // 5MB
        const val MAX_DISK_CACHE_SIZE = 250 * 1024 * 1024 // 250MB
    }
}
