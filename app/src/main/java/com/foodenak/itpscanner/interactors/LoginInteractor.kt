package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
class LoginInteractor(val model: UserModel) : Interactor<User, User> {
    override fun execute(args: User): Observable<User> {
        return model.login(args)
    }
}