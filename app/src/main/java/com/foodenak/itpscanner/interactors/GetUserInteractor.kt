package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/11/2015.
 */
class GetUserInteractor(val model: UserModel) : Interactor<User, String> {
    override fun execute(args: String): Observable<User> {
        return model.getUser(args)
    }
}