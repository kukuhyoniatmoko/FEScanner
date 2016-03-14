package com.foodenak.itpscanner.interactors

import android.accounts.Account
import android.content.Context
import com.foodenak.itpscanner.entities.GoogleCredential
import com.google.android.gms.auth.GoogleAuthUtil
import rx.Observable

/**
 * Created by ITP on 10/7/2015.
 */
class GetGoogleCredential : Interactor<GoogleCredential, GetGoogleCredential.Argument> {
    override fun execute(args: Argument): Observable<GoogleCredential> {
        return Observable.create { subscriber ->
            val token = GoogleAuthUtil.getToken(args.context, args.account, WEB_COMPONENT_SCOPE)
            subscriber.onNext(GoogleCredential(token))
            subscriber.onCompleted()
        }
    }

    class Argument(val account: Account, val context: Context)

    companion object {

        private val WEB_COMPONENT_SCOPE = "audience:server:client_id:233749399763-iolj614o0ichjit2ubfclql2f7t3e4be.apps.googleusercontent.com"

    }
}