package com.foodenak.itpscanner.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/9/2015.
 */
class AlertDialogFragment : DialogFragment() {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
    if (arguments.containsKey(TITLE)) builder.setTitle(arguments.getString(TITLE))
    builder.setMessage(arguments.getString(MESSAGE))
    builder.setPositiveButton(R.string.ok, { dialogInterface, i -> dismiss() })
    return builder.create()
  }

  companion object {

    internal const val MESSAGE = "MESSAGE"
    internal const val TITLE = "TITLE"

    fun newInstance(message: String, title: String? = null): AlertDialogFragment {
      val args = Bundle()
      args.putString(MESSAGE, message)
      if (!TextUtils.isEmpty(title)) args.putString(TITLE, title)
      val fragment = AlertDialogFragment()
      fragment.arguments = args
      return fragment
    }
  }
}
