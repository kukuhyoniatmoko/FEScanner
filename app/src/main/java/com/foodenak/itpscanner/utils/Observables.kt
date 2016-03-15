package com.foodenak.itpscanner.utils

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by ITP on 10/6/2015.
 */
private val SCHEDULER: Observable.Transformer<Any, Any> = Observable.Transformer<Any, Any> { observable ->
  observable.subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.applyScheduler(): Observable<T> {
  return this.compose<T>(SCHEDULER as (Observable.Transformer<T, T>))
}

fun <T> Observable<T>.retryWithBackOff(): Observable<T> {
  var runCount = 0.toLong();
  return retryWhen { it.flatMap { Observable.timer(++runCount * 100, TimeUnit.MILLISECONDS) } }
}