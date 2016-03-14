package com.foodenak.itpscanner.services

import com.foodenak.itpscanner.utils.Devices
import com.foodenak.itpscanner.utils.TokenFactory
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/7/2015.
 */
@Singleton class FERequestInterceptor @Inject constructor(val factory: TokenFactory) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response? {
        val builder = chain.request().newBuilder()
        builder.addHeader(ACCEPT, ACCEPT_VALUE)
        builder.addHeader(ANDROID, Devices.deviceName)
        val token = factory.getToken();
        builder.addHeader(TokenFactory.ENTITY_TOKEN, token.apiToken)
        builder.addHeader(TokenFactory.ENTITY_TIMESTAMP, token.apiTokenTimeStamp)
        val session = UserSession.currentSession;
        if (session.isActive()) {
            builder.addHeader(UserSession.USER_HASH_ID, session.id)
            builder.addHeader(UserSession.USER_ACCESS_TOKEN, session.token)
        }
        val request = builder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val ANDROID = "android"
        private const val ACCEPT = "Accept"
        private const val ACCEPT_VALUE = "application/json"
    }
}
