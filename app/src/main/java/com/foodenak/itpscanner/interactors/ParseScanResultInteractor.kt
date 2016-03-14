package com.foodenak.itpscanner.interactors

import com.google.gson.JsonParser
import rx.Observable

/**
 * Created by ITP on 10/8/2015.
 */
class ParseScanResultInteractor : Interactor<ParseScanResultInteractor.Result, String> {
    override fun execute(args: String): Observable<Result> {
        return Observable.create { subscriber ->
            try {
                val element = JsonParser().parse(args)
                val obj = element.asJsonObject
                val userId = obj.get("user_id").asString
                val deviceId = obj.get("device_id").asString
                subscriber.onNext(Result(userId, deviceId))
                subscriber.onCompleted()
            } catch(e: Exception) {
                subscriber.onError(e)
            }
        }
    }

    data class Result(var userId: String, var deviceId: String)
}
