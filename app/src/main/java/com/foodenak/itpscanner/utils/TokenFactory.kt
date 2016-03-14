package com.foodenak.itpscanner.utils

import android.util.Log
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenFactory @Inject constructor() {

    private var tokenTime: Date = Date()

    private var token: Token? = null

    private fun hashToHmacSHA1(text: String, key: String): String {
        var encrypted = ""
        try {
            val mac = Mac.getInstance(ALGORITHM)
            val secret = SecretKeySpec(key.toByteArray(), ALGORITHM)
            mac.init(secret)
            val digest = mac.doFinal(text.toByteArray())

            val hash = StringBuilder()
            for (aDigest in digest) {
                val hex = Integer.toHexString(255 and aDigest.toInt())
                if (hex.length == 1) {
                    hash.append('0')
                }
                hash.append(hex)
            }

            encrypted = hash.toString()
        } catch (e: Exception) {
            Log.e(TAG, "fail encrypt token", e)
        }

        return encrypted
    }

    fun getToken(): Token {
        refreshTokenIfNeeded()
        return token!!;
    }

    private fun initialize() {
        tokenTime = Date()
        val formattedDate = formattedDate()
        val apiToken = hashToHmacSHA1(formattedDate, createKey(formattedDate))
        token = Token(apiToken, formattedDate)
    }

    @Synchronized private fun refreshTokenIfNeeded() {
        if (isTokenExpired()) initialize()
    }

    private fun isTokenExpired(): Boolean {
        if (token == null) return true
        val currentTime = Date()
        return (TIME_45_MINUTES + tokenTime.time) < currentTime.time
    }

    private fun formattedDate(): String = DateFormats.format(tokenTime)

    private fun createKey(text: String): String {
        return API_KEY + text
    }

    data class Token(val apiToken: String, val apiTokenTimeStamp: String)

    companion object {

        val ENTITY_TOKEN = "api-token"

        val ENTITY_TIMESTAMP = "api-token-timestamp"

        private val ALGORITHM = "HmacSHA1";

        private val TAG = "TokenFactory"

        private val API_KEY = "foodenak"

        private val TIME_45_MINUTES = 1000 * 60 * 60.toLong()
    }
}
