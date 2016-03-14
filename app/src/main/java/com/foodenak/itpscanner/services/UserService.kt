package com.foodenak.itpscanner.services

import com.foodenak.itpscanner.entities.FacebookCredential
import com.foodenak.itpscanner.entities.GoogleCredential
import com.foodenak.itpscanner.entities.TwitterCredential
import com.foodenak.itpscanner.entities.User
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
interface UserService {

    @POST("user/login")
    fun login(@Header("Authorization") auth: String, @Body dummy: RequestBody): Observable<ResponseWrapper<User>>

    @POST("user/login")
    fun loginWithTwitter(@Body credential: TwitterCredential): Observable<ResponseWrapper<User>>

    @POST("user/login")
    fun loginWithGoogle(@Body credential: GoogleCredential): Observable<ResponseWrapper<User>>

    @POST("user/login")
    fun loginWithFacebook(@Body credential: FacebookCredential): Observable<ResponseWrapper<User>>

    @PUT("user")
    fun editProfile(@Body user: User): Observable<ResponseWrapper<User>>

    @PUT("user/update-password")
    fun editPassword(@Body passwordMap: Map<String, String>): Observable<ResponseWrapper<User>>
}
