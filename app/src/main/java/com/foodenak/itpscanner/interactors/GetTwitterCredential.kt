package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.TwitterCredential
import com.foodenak.itpscanner.services.CustomTwitterClient
import com.twitter.sdk.android.core.TwitterSession
import rx.Observable

/**
 * Created by ITP on 10/6/2015.
 */
class GetTwitterCredential : Interactor<TwitterCredential, GetTwitterCredential.Argument> {
    override fun execute(args: Argument): Observable<TwitterCredential> {
        val observable = Observable.create<TwitterCredential> { subscriber ->
            val twitterClient = CustomTwitterClient(args.session)
            val user = twitterClient.getObservableService().show(args.session.userId)

            val credential = TwitterCredential();
            credential.name = user.name;
            credential.twId = user.idStr;
            credential.email = args.email;
            credential.twitterAccessToken = args.session.authToken.token
            credential.twitterAccessTokenSecret = args.session.authToken.secret
            credential.twitterProfilePicture = user.profileImageUrl
            subscriber.onNext(credential)
            subscriber.onCompleted()
        }
        return observable;
    }

    data class Argument(val session: TwitterSession, val email: String?)
}