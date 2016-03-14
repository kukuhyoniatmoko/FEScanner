package com.foodenak.itpscanner.ui.events

import com.foodenak.itpscanner.entities.Event

/**
 * Created by ITP on 10/7/2015.
 */
interface EventsView {
    fun setEvents(data: List<Event>?)

    fun showErrorMessage()

    fun finishRefresh()

    fun showSelectEventConfirmation(event: Event)

    fun notifyEventSelected(event: Event)
}