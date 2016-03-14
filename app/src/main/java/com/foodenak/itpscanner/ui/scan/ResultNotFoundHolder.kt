package com.foodenak.itpscanner.ui.scan

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_result_not_found.view.clearQueryButton
import kotlinx.android.synthetic.main.list_item_result_not_found.view.queryTextView

/**
 * Created by ITP on 10/13/2015.
 */
class ResultNotFoundHolder(view: View) : RecyclerView.ViewHolder(view) {

    val queryTextView = itemView.queryTextView

    val clearQueryButton = itemView.clearQueryButton
}