package com.foodenak.itpscanner.ui.events

import android.support.v4.widget.SwipeRefreshLayout
import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.interactors.GetEventsInteractor
import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.utils.applyScheduler
import rx.functions.Action1
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/7/2015.
 */
@Singleton
class EventsViewModel @Inject constructor(val model: EventModel) {

    var view: EventsView? = null;

    var subscription: CompositeSubscription? = null

    var lastClickedEvent: Event? = null

    fun takeView(view: EventsView) {
        if (this.view == view) {
            return;
        }
        if (subscription != null) {
            subscription!!.unsubscribe();
        }
        subscription = CompositeSubscription();
        loadEvents()
        this.view = view;
    }

    fun dropView(view: EventsView) {
        if (this.view == view) {
            this.view = null;
            if (subscription != null) {
                subscription!!.unsubscribe();
                subscription = null;
            }
        }
    }

    val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        loadEvents()
    }

    val itemClickListener = Action1<Event> { event ->
        lastClickedEvent = event
        view?.showSelectEventConfirmation(event)
    }

    val positiveConfirmationListener = Runnable {
        view?.notifyEventSelected(lastClickedEvent!!)
    }

    private fun loadEvents() {
        subscription!!.add(GetEventsInteractor(model).execute(0)
                .applyScheduler()
                .subscribe({ result ->
                    view?.setEvents(result)
                }, { error ->
                    view?.showErrorMessage()
                }, { view?.finishRefresh() }))
    }
}