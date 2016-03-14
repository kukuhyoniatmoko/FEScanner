package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.RegisterForEventParameter
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.EventModel
import rx.Observable

/**
 * Created by ITP on 10/8/2015.
 */
class RegisterForEventInteractor(val model: EventModel) : Interactor<User, RegisterForEventInteractor.Argument> {
    override fun execute(args: Argument): Observable<User> {
        val param = RegisterForEventParameter(args.userId, args.deviceId)
        return model.register(args.eventId, param)
    }

    data class Argument(val eventId: Long, val userId: String, val deviceId: String? = null)
}