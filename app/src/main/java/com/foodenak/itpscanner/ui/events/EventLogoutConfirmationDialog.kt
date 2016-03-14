package com.foodenak.itpscanner.ui.events

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.ui.scan.ScanActivity

/**
 * Created by ITP on 10/7/2015.
 */
class EventLogoutConfirmationDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setMessage(R.string.logout_confirmation_message)
        builder.setPositiveButton(R.string.ok, { dialogInterface, i ->
            UserSession.initialize(UserSession("", ""), activity)
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            preferences.edit()
                    .putLong(ScanActivity.EXTRA_EVENT_ID, 0)
                    .putString(ScanActivity.EXTRA_EVENT_NAME, "")
                    .commit()
            val intent = Intent()
            intent.putExtra(EventsActivity.RESULT_SHOULD_LOGOUT, true)
            activity.setResult(Activity.RESULT_CANCELED, intent)
            ActivityCompat.finishAfterTransition(activity)
            dismiss()
        })
        builder.setNegativeButton(R.string.cancel, { dialogInterface, i -> dismiss() })
        return builder.create()
    }
}