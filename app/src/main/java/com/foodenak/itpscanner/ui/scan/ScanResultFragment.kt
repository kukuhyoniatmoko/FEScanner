package com.foodenak.itpscanner.ui.scan

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.utils.obtainActivityComponent
import kotlinx.android.synthetic.main.fragment_scan_starter.*
import javax.inject.Inject

/**
 * Created by ITP on 10/6/2015.
 */
class ScanResultFragment : Fragment() {

  @Inject lateinit var viewModel: ScanViewModel

  var userId: String? = null

  var deviceId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activity.obtainActivityComponent<Component>().inject(this)
    userId = arguments.getString(USER_ID)
    deviceId = arguments.getString(DEVICE_ID)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater?.inflate(R.layout.fragment_scan_starter, container, false);
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bindView()
  }

  private fun bindView() {
    rescanScanButton.setOnClickListener(viewModel.startScanButtonClickListener)
    srSubmitButton.setOnClickListener(viewModel.scanSubmitButtonListener)
    userIdTextView.text = if (TextUtils.isEmpty(userId)) getString(R.string.undefined) else userId
    deviceIdTextView.text = if (TextUtils.isEmpty(deviceId)) getString(
        R.string.undefined) else deviceId
  }

  companion object {

    private const val DEVICE_ID = "DEVICE_ID";

    private const val USER_ID = "USER_ID"

    fun newInstance(userId: String, deviceId: String): ScanResultFragment {
      val args = Bundle()
      args.putString(USER_ID, userId)
      args.putString(DEVICE_ID, deviceId)
      val fragment = ScanResultFragment()
      fragment.arguments = args
      return fragment
    }
  }
}