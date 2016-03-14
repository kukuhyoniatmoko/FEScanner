package com.foodenak.itpscanner.services

import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.User
import retrofit.http.GET
import retrofit.http.Query

/**
 * Created by ITP on 10/6/2015.
 */
class CustomTwitterClient(val session: TwitterSession) : TwitterApiClient(session) {

    fun getObservableService(): ObservableService {
        return getService(ObservableService::class.java)
    }

    interface ObservableService {

        @GET("/1.1/users/show.json")
        fun show(@Query("user_id") userId: Long): User
    }
}