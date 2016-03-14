package com.foodenak.itpscanner.ui.redeem

import android.view.View
import android.widget.CompoundButton
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.interactors.RedeemInteractor
import com.foodenak.itpscanner.interactors.RegisterForEventInteractor
import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.services.EVENT_INSUFFICIENT_VOUCHER
import com.foodenak.itpscanner.services.INVALID_CREDENTIAL
import com.foodenak.itpscanner.services.INVALID_EVENT_DEVICE_ID
import com.foodenak.itpscanner.services.exception.ResponseException
import com.foodenak.itpscanner.utils.applyScheduler
import rx.Observable
import rx.Observer
import rx.Subscription

/**
 * Created by ITP on 10/9/2015.
 */
class RedeemViewModel(val userId: String, val eventId: Long, val deviceId: String?, val model: EventModel, val userModel: UserModel) {

    var initialVoucher = false

    var initialLuckyDip = false

    var voucher = false

    var luckyDip = false

    var user: User? = null

    var view: RedeemView? = null

    private var redeemSubscription: Subscription? = null

    private var redeemObservable: Observable<User>? = null

    val backButtonClickListener = {
        if (initialLuckyDip == luckyDip && initialVoucher == voucher) {
            view?.showGoBackConfirmation()
        } else if (initialLuckyDip != luckyDip || initialVoucher != voucher) {
            view?.showChangeWilBeDiscardedConfirmation()
        } else view?.notifyRedeemCancelled()
    }

    val unSelectVoucherListener = { performRedeem() }

    val goBackListener = { view?.notifyRedeemCancelled() }

    val voucherSwitchListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
        voucher = b
    }

    val luckyDibSwitchListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
        luckyDip = b
    }

    val submitButtonClickListener = View.OnClickListener {
        if (initialLuckyDip == luckyDip && initialVoucher == voucher) {
            view!!.showNoChangeHasBeenMadeMessage()
            return@OnClickListener
        }
        if ((initialLuckyDip == true && luckyDip == false) || (initialVoucher == true && voucher == false)) {
            view!!.showUserNeverReceiveVoucherConfirmation()
            return@OnClickListener
        }
        performRedeem()
    }

    private fun performRedeem() {
        view?.showProgressIndicator()
        redeemSubscription?.unsubscribe()
        if (redeemObservable == null) {
            redeemObservable = RedeemInteractor(model).execute(RedeemInteractor.Argument(eventId, userId, voucher, luckyDip))
                    .cache()
        }
        redeemSubscription = redeemObservable!!
                .applyScheduler()
                .subscribe(redeemObserver)
    }

    private val redeemObserver = object : Observer<User> {
        override fun onNext(t: User) {
            view!!.notifyRedeemSuccess(t)
            view?.hideProgressIndicator()
        }

        override fun onError(e: Throwable) {
            if (e is ResponseException) {
                when (e.getStatus()) {
                    INVALID_CREDENTIAL -> view?.notifyInvalidUserId()
                    EVENT_INSUFFICIENT_VOUCHER -> view?.showVoucherEmptyMessage()
                    else -> view?.notifyUnknownError()
                }
            } else {
                view?.notifyUnknownError()
            }
            view?.hideProgressIndicator()
            redeemObservable = null
        }

        override fun onCompleted() {
            redeemObservable = null
        }

    }

    fun takeView(view: RedeemView) {
        if (this.view == view) {
            return
        }
        this.view = view
        if (user != null) {
            view.bindInitialVoucher(initialVoucher, initialLuckyDip)
            view.bindUser(user!!)
            view.hideProgressIndicator()
            if (redeemObservable != null) {
                redeemSubscription = redeemObservable!!
                        .applyScheduler()
                        .subscribe(redeemObserver)
            }
        } else {
            registerForEvent()
        }
    }

    private fun registerForEvent() {
        view!!.showProgressIndicator()
        RegisterForEventInteractor(model).execute(RegisterForEventInteractor.Argument(eventId, userId, deviceId))
                .applyScheduler()
                .subscribe({ user ->
                    this.user = user
                    initialLuckyDip = user.pivot?.redeemLuckydipAt != null
                    initialVoucher = user.pivot?.redeemVoucherAt != null
                    this.view?.bindInitialVoucher(initialVoucher, initialLuckyDip)
                    this.view?.bindUser(user)
                    this.view?.hideProgressIndicator()
                }, { error ->
                    if (error is ResponseException) {
                        when (error.getStatus()) {
                            INVALID_CREDENTIAL -> this.view?.notifyInvalidUserId()
                            INVALID_EVENT_DEVICE_ID -> this.view?.notifyDeviceIdRedeemed()
                            else -> this.view?.notifyUnknownError()
                        }
                    } else {
                        this.view?.notifyUnknownError()
                    }
                    this.view?.hideProgressIndicator()
                })
    }

    fun dropView(view: RedeemView) {
        if (this.view == view) {
            this.view == null
            redeemSubscription?.unsubscribe()
            redeemSubscription = null
        }
    }
}