package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.UserModel
import rx.Observable

/**
 * Created by ITP on 10/12/2015.
 */
class EditPasswordInteractor(val model: UserModel) : Interactor<User, EditPasswordInteractor.Argument> {
    override fun execute(args: Argument): Observable<User> {
        return model.editPassword(args.password, args.newPassword)
    }

    data class Argument(val password: String, val newPassword: String)
}