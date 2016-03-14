package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.model.EventModel
import rx.Observable

/**
 * Created by ITP on 10/6/2015.
 */
class GetEventByIdInteractor(val model: EventModel) : Interactor<Event, Long> {
    override fun execute(args: Long): Observable<Event> {
        return model.getEvent(args)
    }
}