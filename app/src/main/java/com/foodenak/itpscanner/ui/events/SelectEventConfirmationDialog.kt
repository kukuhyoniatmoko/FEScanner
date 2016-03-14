package com.foodenak.itpscanner.ui.events

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.format.DateUtils
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.utils.obtainActivityComponent
import javax.inject.Inject

/**
 * Created by ITP on 10/8/2015.
 */
class SelectEventConfirmationDialog : DialogFragment() {

    var viewModel: EventsViewModel? = null
        @Inject set

    var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.obtainActivityComponent<Component>().inject(this)
        event = viewModel!!.lastClickedEvent
        if (event == null) {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (event == null) {
            return super.onCreateDialog(savedInstanceState)
        }
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)

        val date = DateUtils.formatDateTime(context, event!!.startDate!!.time,
                DateUtils.FORMAT_ABBREV_ALL or DateUtils.FORMAT_SHOW_YEAR)

        builder.setTitle("${event?.name} - $date")
        builder.setMessage(R.string.select_event_confirmation_message)
        builder.setNegativeButton(R.string.cancel, { dialogInterface, i -> dismiss() })
        builder.setPositiveButton(R.string.ok, { dialogInterface, i ->
            viewModel!!.positiveConfirmationListener.run()
            dismiss()
        })
        return builder.create()
    }
}