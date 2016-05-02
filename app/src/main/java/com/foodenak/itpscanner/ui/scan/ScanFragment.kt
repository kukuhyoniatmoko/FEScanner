package com.foodenak.itpscanner.ui.scan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v13.app.FragmentCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.abhi.barcode.frag.libv2.BarcodeFragment
import com.abhi.barcode.frag.libv2.IScanResultHandler
import com.abhi.barcode.frag.libv2.ScanResult
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.ui.AlertDialogFragment
import com.foodenak.itpscanner.ui.redeem.RedeemActivity
import com.foodenak.itpscanner.utils.obtainActivityComponent
import com.google.zxing.BarcodeFormat
import kotlinx.android.synthetic.main.fragment_scan.*
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by ITP on 10/7/2015.
 */
class ScanFragment : Fragment(), FragmentCompat.OnRequestPermissionsResultCallback, IScanResultHandler, ScanView {

  private var pendingReplace: Boolean = false;

  private var pendingReplaceDenied: Boolean = false;

  @Inject lateinit var viewModel: ScanViewModel

  @Inject lateinit var callback: Callback

  private var eventId = 0.toLong()

  private var userId: String? = ""

  private var registerErrorMessage: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    activity.obtainActivityComponent<Component>().inject(this)
    viewModel.eventId = eventId
    if (savedInstanceState != null) {
      userId = savedInstanceState.getString(STATE_SHOULD_SHOW_REDEEM_OPTIONS, "")
    }
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    outState?.putString(STATE_SHOULD_SHOW_REDEEM_OPTIONS, userId)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    val view = inflater?.inflate(R.layout.fragment_scan, container, false)

    val configuration = resources.configuration
    val orientation = configuration.orientation

    val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
    val size = Point();
    display.getSize(size);
    val width = size.x;
    val scanFrame = view?.findViewById(R.id.scan_frame);

    val params = scanFrame?.layoutParams
    params?.height = if (orientation == Configuration.ORIENTATION_LANDSCAPE) (width / 2) else (width)
    scanFrame?.layoutParams = params
    return view;
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bindView()
  }

  private fun bindView() {
    checkButton.setOnClickListener(viewModel.submitButtonClickListener)
    uidEditText.addTextChangedListener(viewModel.userIdTextWatcher)
    deviceEditText.addTextChangedListener(viewModel.deviceIdTextWatcher)
    deviceEditText.setImeActionLabel(getText(R.string.submit), EditorInfo.IME_ACTION_GO)
    deviceEditText.setOnEditorActionListener(viewModel.editorActionListener)
    setScanResultHandlerIfNeeded()
  }

  private fun setScanResultHandlerIfNeeded() {
    val fragment = childFragmentManager.findFragmentById(R.id.scan_frame);
    if (fragment is BarcodeFragment) {
      fragment.scanResultHandler = this
    }
  }

  private fun replaceWithCheck() {
    if (ContextCompat.checkSelfPermission(context,
        ScanActivity.PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
      replace();
    } else {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
          ScanActivity.PERMISSIONS[0])) {
        val manager = childFragmentManager;
        val fragment = manager.findFragmentByTag(ScanActivity.RATIONAL_FRAGMENT_TAG);
        if (fragment !is ScanRationaleFragment) {
          ScanRationaleFragment().show(manager, ScanActivity.RATIONAL_FRAGMENT_TAG)
        }
      } else {
        requestPermissions(ScanActivity.PERMISSIONS, ScanActivity.PERMISSIONS_REQUEST)
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
      grantResults: IntArray) {
    when (requestCode) {
      ScanActivity.PERMISSIONS_REQUEST -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (isResumed) {
            replace()
          } else {
            pendingReplace = true;
          }
        } else {
          Toast.makeText(context, R.string.camera_permission_denied_message,
              Toast.LENGTH_LONG).show()
          if (isResumed) {
            replaceDenied()
          }
        }
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }

  private fun replaceDenied() {
    val fragment = childFragmentManager.findFragmentById(R.id.scan_frame)
    if (fragment !is ScanEnablerFragment) {
      childFragmentManager.beginTransaction()
          .replace(R.id.scan_frame, ScanEnablerFragment())
          .commit()
    }
  }

  private fun replace() {
    val manager = childFragmentManager;
    val currentFragment = manager.findFragmentById(R.id.scan_frame)
    if (currentFragment is BarcodeFragment) {
      currentFragment.scanResultHandler = this
      currentFragment.restart()
      return
    }
    val fragment = BarcodeFragment();
    fragment.scanResultHandler = this;
    fragment.setDecodeFor(EnumSet.of(BarcodeFormat.QR_CODE))
    manager.beginTransaction()
        .replace(R.id.scan_frame, fragment)
        .commit()
  }

  override fun scanResult(p0: ScanResult) {
    viewModel.scanResultListener.scanResult(p0.parsedResult.displayResult)
  }

  override fun showScanResult(userId: String, deviceId: String) {
    childFragmentManager.beginTransaction()
        .replace(R.id.scan_frame, ScanResultFragment.newInstance(userId, deviceId))
        .commit()
  }

  override fun showInvalidQRCodeMessage() {
    childFragmentManager.beginTransaction()
        .replace(R.id.scan_frame, InvalidQRCodeFragment())
        .commit()
  }

  override fun showUserIdShouldNotEmptyMessage() {
    uidInputLayout.error = getString(R.string.uid_required)
    uidEditText.requestFocus()
  }

  override fun hideUserIdShouldNotEmptyMessage() {
    uidInputLayout.error = null
  }

  override fun showDeviceIdShouldNotEmptyMessage() {
    deviceInputLayout.error = getString(R.string.device_id_required)
    deviceEditText.requestFocus()
  }

  override fun hideDeviceIdShouldNotEmptyMessage() {
    deviceInputLayout.error = null
  }

  override fun showRegisterFailedMessage(message: String) {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(message).show(manager, ERROR_DIALOG_TAG)
  }

  override fun navigateToOptions(userId: String, deviceId: String?) {
    val intent = Intent(context, RedeemActivity::class.java)
    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    eventId = preferences.getLong(ScanActivity.EXTRA_EVENT_ID, 0)
    intent.putExtra(RedeemActivity.USER_ID, userId)
    intent.putExtra(RedeemActivity.DEVICE_ID, deviceId)
    intent.putExtra(RedeemActivity.EVENT_ID, eventId)
    startActivityForResult(intent, REQUEST_REDEEM)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_REDEEM -> {
        if (resultCode == Activity.RESULT_OK) {
          userId = data!!.getStringExtra(RedeemActivity.USER_ID)
          if (isResumed) {
            viewModel.redeemSuccessListener.call(userId)
            userId = ""
          }
        } else {
          registerErrorMessage = data?.getStringExtra(RedeemActivity.ERROR_MESSAGE)
        }
        return
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun showLoadingIndicator() {
    progressBar.visibility = View.VISIBLE
    scrollView.visibility = View.GONE
  }

  override fun hideLoadingIndicator() {
    progressBar.visibility = View.GONE
    scrollView.visibility = View.VISIBLE
  }

  override fun showEditRedeemOptions(user: User) {
    val snackText = StringBuilder()
    snackText.append(user.name)
    snackText.append(System.getProperty("line.separator"))
    val pivot = user.pivot
    if (pivot != null) if (pivot.redeemLuckydipAt != null || pivot.redeemVoucherAt != null) {
      if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt == null) {
        snackText.append(getString(R.string.lucky_dip_redeemed))
      } else if (pivot.redeemLuckydipAt == null && pivot.redeemVoucherAt != null) {
        snackText.append(getString(R.string.voucher_redeemed))
      } else if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt != null) {
        snackText.append(getString(R.string.lucky_dip_voucher_redeemed))
      }
    }
    val snack = Snackbar.make(scrollView, snackText.toString(), Snackbar.LENGTH_LONG)
        .setAction(R.string.edit, {
          viewModel.editRedeemOptionsListener.call(user)
        })
    snack.duration = TimeUnit.SECONDS.toMillis(10).toInt()
    snack.show()
  }

  override fun setVoucherRemaining(voucherRemaining: Int) {
    callback.setVoucherRemaining(voucherRemaining)
  }

  override fun onStart() {
    super.onStart()
    val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
    eventId = preferences.getLong(ScanActivity.EXTRA_EVENT_ID, 0)
    if (UserSession.currentSession.isActive() && eventId != 0.toLong()) {
      viewModel.eventId = (eventId)
      viewModel.takeView(this)
    }
  }

  override fun onResume() {
    super.onResume()
    if (pendingReplace) {
      pendingReplace = false
      replace()
    } else if (pendingReplaceDenied) {
      pendingReplaceDenied = false
      replaceDenied()
    } else {
      replaceWithCheck()
    }
    if (registerErrorMessage != null && registerErrorMessage!!.isNotBlank()) {
      viewModel.redeemFailedListener.call(registerErrorMessage)
      registerErrorMessage = null
    }
    if (TextUtils.isEmpty(userId)) {
      return
    }
    viewModel.redeemSuccessListener.call(userId)
    userId = ""
  }

  override fun startScan() {
    val manager = childFragmentManager;
    val currentFragment = manager.findFragmentById(R.id.scan_frame)
    if (currentFragment is BarcodeFragment) {
      currentFragment.scanResultHandler = this
      currentFragment.restart()
    } else replaceWithCheck()
  }

  override fun onStop() {
    super.onStop()
    viewModel.dropView(this)
  }

  companion object {

    internal val ERROR_DIALOG_TAG = "ERROR_DIALOG_TAG"

    internal val REQUEST_REDEEM = 1;

    internal val STATE_SHOULD_SHOW_REDEEM_OPTIONS = "STATE_SHOULD_SHOW_REDEEM_OPTIONS"
  }

  interface Callback {

    fun setVoucherRemaining(count: Int)
  }
}