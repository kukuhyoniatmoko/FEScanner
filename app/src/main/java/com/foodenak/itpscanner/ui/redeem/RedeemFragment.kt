package com.foodenak.itpscanner.ui.redeem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.services.image.ImageLoader
import com.foodenak.itpscanner.ui.AlertDialogFragment
import com.foodenak.itpscanner.utils.obtainActivityComponent
import kotlinx.android.synthetic.main.fragment_redeem.contentView
import kotlinx.android.synthetic.main.fragment_redeem.headerBackground
import kotlinx.android.synthetic.main.fragment_redeem.luckyDipSwitch
import kotlinx.android.synthetic.main.fragment_redeem.progressBar
import kotlinx.android.synthetic.main.fragment_redeem.submitButton
import kotlinx.android.synthetic.main.fragment_redeem.userName
import kotlinx.android.synthetic.main.fragment_redeem.userPhoto
import kotlinx.android.synthetic.main.fragment_redeem.userUsername
import kotlinx.android.synthetic.main.fragment_redeem.voucherSwitch
import javax.inject.Inject

/**
 * Created by ITP on 10/9/2015.
 */
class RedeemFragment : Fragment(), RedeemView {

    @Inject lateinit var viewModel: RedeemViewModel

    @Inject lateinit var imageLoader: ImageLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.obtainActivityComponent<Component>().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_redeem, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        voucherSwitch.setOnCheckedChangeListener(viewModel.voucherSwitchListener)
        luckyDipSwitch.setOnCheckedChangeListener(viewModel.luckyDibSwitchListener)
        submitButton.setOnClickListener(viewModel.submitButtonClickListener)
    }

    override fun onStart() {
        super.onStart()
        viewModel.takeView(this)
    }

    override fun bindUser(user: User) {
        if ( user.thumbUrl?.original == null) {
            userPhoto.setImageDrawable(null)
            headerBackground.setImageDrawable(null)
        } else {
            imageLoader.load(user.thumbUrl!!.original!!, userPhoto)
            imageLoader.load(user.thumbUrl!!.original!!, headerBackground)
        }
        userName.text = user.name
        userUsername.text = "@${user.username}"
    }

    override fun bindInitialVoucher(initialVoucher: Boolean, initialLuckyDip: Boolean) {
        if (!luckyDipSwitch.isChecked) luckyDipSwitch.isChecked = initialLuckyDip
        if (!voucherSwitch.isChecked) voucherSwitch.isChecked = initialVoucher
    }

    override fun notifyDeviceIdRedeemed() {
        val result = Intent()
        result.putExtra(RedeemActivity.ERROR_MESSAGE, getString(R.string.device_id_redeemed))
        activity.setResult(Activity.RESULT_CANCELED, result)
        activity.supportFinishAfterTransition()
    }

    override fun showProgressIndicator() {
        progressBar.visibility = View.VISIBLE
        contentView.visibility = View.GONE
    }

    override fun showNoChangeHasBeenMadeMessage() {
        val manager = childFragmentManager
        (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
        AlertDialogFragment.newInstance(getString(R.string.no_change_message)).show(manager, ERROR_DIALOG_TAG)
    }

    override fun hideProgressIndicator() {
        progressBar.visibility = View.GONE
        contentView.visibility = View.VISIBLE
    }

    override fun notifyRedeemSuccess(user: User) {
        val result = Intent()
        result.putExtra(RedeemActivity.USER_ID, user.hashId)
        activity.setResult(Activity.RESULT_OK, result)
        activity.supportFinishAfterTransition()
    }

    override fun notifyInvalidUserId() {
        val result = Intent()
        result.putExtra(RedeemActivity.ERROR_MESSAGE, getString(R.string.invalid_user_id))
        activity.setResult(Activity.RESULT_CANCELED, result)
        activity.supportFinishAfterTransition()
    }

    override fun showVoucherEmptyMessage() {
        val manager = childFragmentManager
        (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
        AlertDialogFragment.newInstance(getString(R.string.voucher_empty_message)).show(manager, ERROR_DIALOG_TAG)
    }

    override fun notifyUnknownError() {
        val result = Intent()
        result.putExtra(RedeemActivity.ERROR_MESSAGE, getString(R.string.something_went_wrong))
        activity.setResult(Activity.RESULT_CANCELED, result)
        activity.supportFinishAfterTransition()
    }

    override fun showGoBackConfirmation() {
        val manager = childFragmentManager
        (manager.findFragmentByTag(CONFIRMATION_DIALOG_TAG) as DialogFragment?)?.dismiss()
        GoBackConfirmationFragment.newInstance(getString(R.string.back_confirmation_message)).show(manager, CONFIRMATION_DIALOG_TAG)
    }

    override fun showUserNeverReceiveVoucherConfirmation() {
        val manager = childFragmentManager
        (manager.findFragmentByTag(CONFIRMATION_DIALOG_TAG) as DialogFragment?)?.dismiss()
        UnSelectVoucherConfirmationFragment().show(manager, CONFIRMATION_DIALOG_TAG)
    }

    override fun showChangeWilBeDiscardedConfirmation() {
        val manager = childFragmentManager
        (manager.findFragmentByTag(CONFIRMATION_DIALOG_TAG) as DialogFragment?)?.dismiss()
        GoBackConfirmationFragment.newInstance(getString(R.string.change_will_be_discarded_message)).show(manager, CONFIRMATION_DIALOG_TAG)
    }

    override fun notifyRedeemCancelled() {
        activity.setResult(Activity.RESULT_CANCELED)
        activity.supportFinishAfterTransition()
    }

    override fun onStop() {
        super.onStop()
        viewModel.dropView(this)
    }

    companion object {

        val ERROR_DIALOG_TAG = "ERROR_DIALOG_TAG"

        val CONFIRMATION_DIALOG_TAG = "CONFIRMATION_DIALOG_TAG"
    }
}