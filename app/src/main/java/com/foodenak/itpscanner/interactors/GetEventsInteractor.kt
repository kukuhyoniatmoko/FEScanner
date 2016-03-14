package com.foodenak.itpscanner.interactors

import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.model.EventModel
import rx.Observable

/**
 * Created by ITP on 10/6/2015.
 */
class GetEventsInteractor(val model: EventModel) : Interactor<List<Event>, Any> {
    override fun execute(args: Any): Observable<List<Event>> {
        val observable = model.getEvents()
        return observable;
    }
}