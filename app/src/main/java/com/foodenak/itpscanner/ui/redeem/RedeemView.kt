package com.foodenak.itpscanner.ui.redeem

import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/9/2015.
 */
interface RedeemView {
    fun bindUser(user: User)

    fun showProgressIndicator()

    fun showNoChangeHasBeenMadeMessage()

    fun hideProgressIndicator()

    fun notifyRedeemSuccess(user: User)

    fun notifyInvalidUserId()

    fun showVoucherEmptyMessage()

    fun notifyUnknownError()

    fun showGoBackConfirmation()

    fun showUserNeverReceiveVoucherConfirmation()

    fun showChangeWilBeDiscardedConfirmation()

    fun notifyRedeemCancelled()

    fun bindInitialVoucher(initialVoucher: Boolean, initialLuckyDip: Boolean)

    fun notifyDeviceIdInvalidOrRedeemed()

    fun showConnectionTimeoutMessage()

    fun showRedeemFailedConnectionTimeoutMessage()

    fun showRedeemFailedNoInternetMessage()

    fun showNoInternetMessage()
}