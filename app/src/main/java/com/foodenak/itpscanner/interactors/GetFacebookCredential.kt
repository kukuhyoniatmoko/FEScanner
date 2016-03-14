package com.foodenak.itpscanner.interactors

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.foodenak.itpscanner.entities.FacebookCredential
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
class GetFacebookCredential : Interactor<FacebookCredential, AccessToken> {
    override fun execute(args: AccessToken): Observable<FacebookCredential> {
        val observable: Observable<FacebookCredential> = Observable.create({ subscriber ->
            val graphRequest = GraphRequest.newMeRequest(args, null);
            val graphResponse = graphRequest.executeAndWait();

            val userObject = graphResponse.jsonObject;

            val fbId = userObject.optString("id");
            val name = userObject.optString("name");

            val emailRequest = GraphRequest(args, "/me");
            val params = Bundle();
            params.putString("fields", "email");
            emailRequest.parameters = params;
            val emailResponse = emailRequest.executeAndWait();

            val emailObject = emailResponse.jsonObject;
            val email = emailObject.optString("email");

            val credential = FacebookCredential();
            credential.fbId = fbId;
            credential.name = name;
            credential.email = email;

            subscriber.onNext(credential);
            subscriber.onCompleted();
        });
        return observable;
    }
}
