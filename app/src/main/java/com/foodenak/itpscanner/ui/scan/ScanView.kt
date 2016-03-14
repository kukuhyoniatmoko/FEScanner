package com.foodenak.itpscanner.ui.scan

import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/6/2015.
 */
interface ScanView {
    fun startScan()

    fun showScanResult(userId: String, deviceId: String)

    fun showInvalidQRCodeMessage()

    fun showUserIdShouldNotEmptyMessage()

    fun showDeviceIdShouldNotEmptyMessage()

    fun navigateToOptions(userId: String, deviceId: String? = null)

    fun showLoadingIndicator()

    fun hideLoadingIndicator()

    fun showEditRedeemOptions(user: User)

    fun setVoucherRemaining(voucherRemaining: Int)

    fun hideUserIdShouldNotEmptyMessage()

    fun hideDeviceIdShouldNotEmptyMessage()

    fun showRegisterFailedMessage(message: String)
}