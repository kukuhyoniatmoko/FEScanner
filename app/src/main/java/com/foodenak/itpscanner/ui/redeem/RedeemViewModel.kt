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
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

  private var registerSubscription: Subscription? = null

  val backButtonClickListener = {
    if (initialLuckyDip == luckyDip && initialVoucher == voucher) {
      view?.showGoBackConfirmation()
    } else if (initialLuckyDip != luckyDip || initialVoucher != voucher) {
      view?.showChangeWilBeDiscardedConfirmation()
    } else view?.notifyRedeemCancelled()
  }

  val unSelectVoucherListener = { performRedeemInternal() }

  val goBackListener = { view?.notifyRedeemCancelled() }

  val voucherSwitchListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
    voucher = b
  }

  val luckyDibSwitchListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
    luckyDip = b
  }

  val submitButtonClickListener = View.OnClickListener { performRedeem() }

  fun performRedeem() {
    if (initialLuckyDip == luckyDip && initialVoucher == voucher) {
      view!!.showNoChangeHasBeenMadeMessage()
    } else if ((initialLuckyDip == true && luckyDip == false) || (initialVoucher == true && voucher == false)) {
      view!!.showUserNeverReceiveVoucherConfirmation()
    } else performRedeemInternal()
  }

  private fun performRedeemInternal() {
    view?.showProgressIndicator()
    redeemSubscription?.unsubscribe()
    val redeemObservable: Observable<User> = redeemObservable ?: RedeemInteractor(model).execute(
        RedeemInteractor.Argument(eventId, userId, voucher, luckyDip)).cache().applyScheduler()
    this.redeemObservable = redeemObservable
    redeemSubscription = redeemObservable.subscribe(redeemObserver)
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
      } else if (e is SocketTimeoutException) {
        view?.showRedeemFailedConnectionTimeoutMessage()
      } else if (e is UnknownHostException) {
        view?.showRedeemFailedNoInternetMessage()
      } else {
        view?.notifyUnknownError()
      }
      view?.hideProgressIndicator()
    }

    override fun onCompleted() {
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
      val redeemObservable = this.redeemObservable
      if (redeemObservable != null) redeemSubscription = redeemObservable.subscribe(redeemObserver)
    } else {
      registerForEvent()
    }
  }

  private fun registerForEvent() {
    view!!.showProgressIndicator()
    registerSubscription = RegisterForEventInteractor(model).execute(
        RegisterForEventInteractor.Argument(eventId, userId, deviceId))
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
              INVALID_EVENT_DEVICE_ID -> this.view?.notifyDeviceIdInvalidOrRedeemed()
              else -> this.view?.notifyUnknownError()
            }
          } else if (error is SocketTimeoutException) {
            this.view?.showConnectionTimeoutMessage()
          } else if (error is UnknownHostException) {
            this.view?.showNoInternetMessage()
          } else {
            this.view?.notifyUnknownError()
          }
          this.view?.hideProgressIndicator()
        })
  }

  fun dropView(view: RedeemView) {
    if (this.view == view) {
      val redeemSubscription = this.redeemSubscription
      if (redeemSubscription == null || redeemSubscription.isUnsubscribed) {
        redeemObservable = null
      } else {
        redeemSubscription.unsubscribe()
      }
      this.redeemSubscription = null
      registerSubscription?.unsubscribe()
      registerSubscription = null
      this.view = null
    }
  }

  fun retryRegistration() {
    registerForEvent()
  }
}