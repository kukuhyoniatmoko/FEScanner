package com.foodenak.itpscanner.utils

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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