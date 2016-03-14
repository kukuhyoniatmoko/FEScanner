package com.foodenak.itpscanner.services

import com.foodenak.itpscanner.entities.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import rx.Observable

/**
 * Created by ITP on 10/8/2015.
 */
interface PoolService {
    @GET("event/{id}/user-history")
    fun getHistory(@Path("id") id: Long, @QueryMap query: Map<String, String>): Observable<ListRedeemWrapper<User>>
}