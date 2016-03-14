package com.foodenak.itpscanner.ui.events

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.foodenak.itpscanner.R

/**
 * Created by ITP on 10/7/2015.
 */
class EventListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val eventTitle: TextView = itemView.findViewById(R.id.eventTitle) as TextView

    val eventTime: TextView = itemView.findViewById(R.id.eventTime) as TextView

    val eventImage: ImageView = itemView.findViewById(R.id.eventImage) as ImageView

    val eventListItem: View = itemView.findViewById(R.id.eventListItem)
}