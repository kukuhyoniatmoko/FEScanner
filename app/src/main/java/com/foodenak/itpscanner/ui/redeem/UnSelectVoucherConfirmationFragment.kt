package com.foodenak.itpscanner.ui.redeem

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.utils.obtainActivityComponent
import javax.inject.Inject

/**
 * Created by ITP on 10/10/2015.
 */
class UnSelectVoucherConfirmationFragment : DialogFragment() {

    @Inject lateinit var viewModel: RedeemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.obtainActivityComponent<Component>().inject(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setMessage(R.string.un_select_voucher_message)
        builder.setPositiveButton(R.string.yes, { dialogInterface, i ->
            viewModel.unSelectVoucherListener.invoke()
            dismiss()
        })
        builder.setNegativeButton(R.string.no, { dialogInterface, i -> dismiss() })
        return builder.create()
    }
}