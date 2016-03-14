package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.GoogleCredential
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
class LoginWithGoogleInteractor(val model: UserModel) : Interactor<User, GoogleCredential> {
    override fun execute(args: GoogleCredential): Observable<User> {
        return model.loginWithGoogle(args)
    }
}
