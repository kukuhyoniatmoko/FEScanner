package com.foodenak.itpscanner.persistence

import android.content.Context
import android.preference.PreferenceManager
import rx.Observable
import rx.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/9/2015.
 */
@Singleton
class VoucherRepository @Inject constructor(val context: Context) {

    val preferences = PreferenceManager.getDefaultSharedPreferences(context);

    val publisher = PublishSubject.create<Int>()

    fun getVoucherRemaining(): Observable<Int> {
        return Observable.merge(Observable.create { subscriber ->
            subscriber.onNext(preferences.getInt(VOUCHER_REMAINING, 0))
            subscriber.onCompleted()
        }, publisher)
    }

    fun setVoucherRemaining(count: Int) {
        preferences.edit().putInt(VOUCHER_REMAINING, count).commit()
        publisher.onNext(count)
    }

    companion object {

        val VOUCHER_REMAINING = "VOUCHER_REMAINING";
    }
}
