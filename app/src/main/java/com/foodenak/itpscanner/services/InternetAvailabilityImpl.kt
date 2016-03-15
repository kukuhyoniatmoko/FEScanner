package com.foodenak.itpscanner.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import com.foodenak.itpscanner.services.InternetAvailability.State
import rx.Observable
import rx.Subscriber
import rx.subscriptions.Subscriptions

/**
 * Created by kukuh on 16/03/14.
 */
class InternetAvailabilityImpl(private val context: Context) : InternetAvailability {
  private val manager = context.getSystemService(
      Context.CONNECTIVITY_SERVICE) as ConnectivityManager

  override fun state(): State {
    return if (manager.activeNetwork == null) {
      State.disconnected
    } else {
      if (manager.isActiveNetworkMetered) State.connected_metered else State.connected_un_metered
    }
  }

  override fun observe(): Observable<State> = Observable.create { OnSubscribe(context, this) }

  class OnSubscribe(private val context: Context, private val availability: InternetAvailability) : Observable.OnSubscribe<State> {

    override fun call(p0: Subscriber<in State>) {
      val receiver = Receiver(p0, availability)
      val filter = IntentFilter()
      filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
      p0.add(Subscriptions.create { context.unregisterReceiver(receiver) })
      context.registerReceiver(receiver, filter)
    }
  }

  class Receiver(private val subscriber: Subscriber<in State>, private val availability: InternetAvailability) : BroadcastReceiver() {
    private var state = availability.state()

    override fun onReceive(p0: Context?, p1: Intent?) {
      val currentState = availability.state()
      if (currentState == state) return
      state = currentState
      subscriber.onNext(state)
    }
  }
}