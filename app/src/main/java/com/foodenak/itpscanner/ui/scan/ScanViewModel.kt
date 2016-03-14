package com.foodenak.itpscanner.ui.scan

import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.interactors.GetUserInteractor
import com.foodenak.itpscanner.interactors.ParseScanResultInteractor
import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.utils.TextWatcherAdapter
import com.foodenak.itpscanner.utils.applyScheduler
import rx.Subscription
import rx.functions.Action1
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/6/2015.
 */
@Singleton
class ScanViewModel @Inject constructor(val model: EventModel, val userModel: UserModel) {
    var eventId: Long = 0

    var view: ScanView? = null;

    var userId = ""

    var deviceId = ""

    var subscription: Subscription? = null

    var voucherRemainingSubscription: Subscription? = null

    val startScanButtonClickListener = View.OnClickListener { view?.startScan() }

    val submitButtonClickListener = View.OnClickListener { validate() }

    val scanSubmitButtonListener = View.OnClickListener { }

    val scanResultListener: ScanResultListener = object : ScanResultListener {
        override fun scanResult(result: String) {
            ParseScanResultInteractor().execute(result)
                    .applyScheduler()
                    .subscribe({ parsedResult ->
                        registerForEvent(parsedResult.userId, parsedResult.deviceId)
                    }, { view?.showInvalidQRCodeMessage() })
        }
    }

    val redeemSuccessListener = Action1<String> { userId ->
        GetUserInteractor(userModel).execute(userId)
                .applyScheduler()
                .subscribe({ user ->
                    view!!.showEditRedeemOptions(user)
                }, { e ->
                    Log.e("ScanViewModel", "fail load user", e)
                })
    }

    val redeemFailedListener = Action1<String> { message ->
        view!!.showRegisterFailedMessage(message)
    }

    val editRedeemOptionsListener = Action1<User> { user ->
        view?.navigateToOptions(userId = user.hashId!!)
    }

    fun registerForEvent(userId: String, deviceId: String) {
        view?.navigateToOptions(userId, deviceId)
    }

    val editorActionListener = TextView.OnEditorActionListener { textView, i, keyEvent ->
        if (i == EditorInfo.IME_ACTION_GO) {
            validate()
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private fun validate() {
        if (TextUtils.isEmpty(userId)) {
            view?.showUserIdShouldNotEmptyMessage()
            return
        }
        if (TextUtils.isEmpty(deviceId)) {
            view?.showDeviceIdShouldNotEmptyMessage()
            return
        }
        registerForEvent(userId, deviceId)
    }

    val userIdTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            userId = s.toString()
            view?.hideUserIdShouldNotEmptyMessage()
        }
    }

    val deviceIdTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            deviceId = s.toString()
            view?.hideDeviceIdShouldNotEmptyMessage()
        }
    }

    fun takeView(view: ScanView) {
        if (this.view == view) {
            return;
        }
        subscription?.unsubscribe()
        this.view = view;
        voucherRemainingSubscription = model.getVoucherRemaining().subscribe({ count ->
            this.view?.setVoucherRemaining(count)
        }, {})
    }

    fun dropView(view: ScanView) {
        if (this.view == view) {
            this.view = null;
            subscription?.unsubscribe()
            voucherRemainingSubscription?.unsubscribe()
            subscription = null
        }
    }
}