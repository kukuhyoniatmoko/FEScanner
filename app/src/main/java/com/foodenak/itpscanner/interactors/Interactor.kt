package com.foodenak.itpscanner.interactors

import rx.Observable

/**
 * Created by ITP on 10/5/2015.
 */
interface Interactor<R, A> {

    fun execute(args: A): Observable<R>;
}