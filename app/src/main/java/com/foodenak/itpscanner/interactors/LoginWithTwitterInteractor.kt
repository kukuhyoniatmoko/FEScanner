package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.TwitterCredential
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
class LoginWithTwitterInteractor(val model: UserModel) : Interactor<User, TwitterCredential> {
    override fun execute(args: TwitterCredential): Observable<User> {
        return model.loginWithTwitter(args)
    }
}
