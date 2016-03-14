package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.HistoryParameter
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.model.EventModel
import rx.Observable

/**
 * Created by ITP on 10/8/2015.
 */
class GetHistoryInteractor(val model: EventModel) : Interactor<List<User>, HistoryParameter> {
    override fun execute(args: HistoryParameter): Observable<List<User>> {
        return model.getHistory(args.eventId, args)
    }
}