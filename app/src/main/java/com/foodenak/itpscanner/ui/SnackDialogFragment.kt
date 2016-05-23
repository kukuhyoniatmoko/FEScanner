package com.foodenak.itpscanner.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/9/2015.
 */
class SnackDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
    if (arguments.containsKey(TITLE)) builder.setTitle(arguments.getString(TITLE))
    builder.setMessage(arguments.getString(MESSAGE))
    builder.setPositiveButton(arguments.getString(POSITIVE_TEXT), this)
    builder.setNegativeButton(arguments.getString(NEGATIVE_TEXT), this)
    return builder.create()
  }

  override fun onClick(dialog: DialogInterface?, which: Int) {
    val parent = parentFragment
    val activity = activity
    val listener: Listener? =
        if (parent is Listener) parent
        else if (activity is Listener) activity
        else null
    listener?.onClick(this, which)
  }

  interface Listener {
    fun onClick(fragment: SnackDialogFragment, which: Int)
  }

  companion object {
    internal const val TITLE = "TITLE";
    internal const val MESSAGE = "MESSAGE"
    internal const val POSITIVE_TEXT = "POSITIVE_TEXT"
    internal const val NEGATIVE_TEXT = "NEGATIVE_TEXT"

    fun newInstance(message: String, okText: String, noText: String): SnackDialogFragment {
      val args = Bundle()
      args.putString(MESSAGE, message)
      args.putString(POSITIVE_TEXT, okText)
      args.putString(NEGATIVE_TEXT, noText)
      val fragment = SnackDialogFragment()
      fragment.arguments = args
      return fragment
    }

    fun newInstance(title: String, message: String, okText: String,
        noText: String): SnackDialogFragment {
      val args = Bundle()
      args.putString(TITLE, title)
      args.putString(MESSAGE, message)
      args.putString(POSITIVE_TEXT, okText)
      args.putString(NEGATIVE_TEXT, noText)
      val fragment = SnackDialogFragment()
      fragment.arguments = args
      return fragment
    }
  }
}
