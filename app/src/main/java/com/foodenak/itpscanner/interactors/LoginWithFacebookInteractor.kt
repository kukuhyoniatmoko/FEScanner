package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.FacebookCredential
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
class LoginWithFacebookInteractor(val model: UserModel) : Interactor<User, FacebookCredential> {
    override fun execute(args: FacebookCredential): Observable<User> {
        return model.loginWithFacebook(args)
    }
}
