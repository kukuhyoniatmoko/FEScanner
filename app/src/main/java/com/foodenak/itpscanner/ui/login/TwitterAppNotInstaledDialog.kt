package com.foodenak.itpscanner.ui.login

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/7/2015.
 */
class TwitterAppNotInstaledDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(R.string.tw_login_failed)
        builder.setMessage(R.string.tw_login_failed_app_not_installed)
        builder.setNegativeButton(R.string.cancel, { dialog, which -> dismiss() })
        builder.setPositiveButton(R.string.install, { dialog, which ->
            val packageName = "com.twitter.android"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)))
            }

            dismiss()
        })
        return builder.create()
    }
}