package com.foodenak.itpscanner.services

import rx.Observable

/**
 * Created by kukuh on 16/03/14.
 */
interface InternetAvailability {

  fun state(): State

  fun observe(): Observable<State>

  enum class State {
    connected_metered, connected_un_metered, disconnected
  }
}