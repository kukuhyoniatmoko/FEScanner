package com.foodenak.itpscanner.ui.scan

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/6/2015.
 */
class ScanRationaleFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setMessage(R.string.camera_permission_rationale_message)
        builder.setPositiveButton(R.string.ok, { dialogInterface, i ->
            val fragment = parentFragment
            if (fragment != null) {
                fragment.requestPermissions(ScanActivity.PERMISSIONS, ScanActivity.PERMISSIONS_REQUEST)
            } else {
                ActivityCompat.requestPermissions(activity, ScanActivity.PERMISSIONS, ScanActivity.PERMISSIONS_REQUEST)
            }
            dismiss()
        })
        return builder.create();
    }
}