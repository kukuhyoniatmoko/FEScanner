package com.foodenak.itpscanner.ui.scan

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.list_item_history.view.dateTextView
import kotlinx.android.synthetic.main.list_item_history.view.nameTextView
import kotlinx.android.synthetic.main.list_item_history.view.profilePhoto
import kotlinx.android.synthetic.main.list_item_history.view.redeemTextView
import kotlinx.android.synthetic.main.list_item_history.view.usernameTextView

/**
 * Created by ITP on 10/10/2015.
 */
class HistoryListItemHolder(view: View) : RecyclerView.ViewHolder(view) {

    val profilePhoto = itemView.profilePhoto

    val dateTextView = itemView.dateTextView

    val nameTextView = itemView.nameTextView

    val usernameTextView = itemView.usernameTextView

    val redeemTextView = itemView.redeemTextView
}