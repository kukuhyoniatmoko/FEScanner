package com.foodenak.itpscanner.services

import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.entities.RedeemParameter
import com.foodenak.itpscanner.entities.RegisterForEventParameter
import com.foodenak.itpscanner.entities.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap
import rx.Observable

/**
 * Created by ITP on 10/6/2015.
 */
interface EventService {

    @GET("event")
    fun getEvents(): Observable<ListResponseWrapper<Event>>

    @GET("event/{id}")
    fun getEvent(@Path("id") id: Long): Observable<ResponseWrapper<Event>>

    @GET("event/{id}/user-history")
    fun getHistory(@Path("id") id: Long, @QueryMap query: Map<String, String>): Observable<ListRedeemWrapper<User>>

    @POST("event/{id}/user-register")
    fun register(@Path("id") id: Long, @Body parameter: RegisterForEventParameter): Observable<ResponseWrapper<User>>

    @POST("event/{id}/user-redeem")
    fun redeem(@Path("id") id: Long, @Body parameter: RedeemParameter): Observable<RedeemWrapper<User>>
}