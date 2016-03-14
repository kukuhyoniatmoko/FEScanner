package com.foodenak.itpscanner.ui.events

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.Event
import com.squareup.picasso.Picasso

/**
 * Created by ITP on 10/7/2015.
 */
class EventsAdapter : RecyclerView.Adapter<EventListItemViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private var events: List<Event>? = null;

    var viewModel: EventsViewModel? = null

    fun setItems(events: List<Event>?) {
        if (this.events == events) {
            return
        }
        this.events = events
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return events?.get(position)?.id ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListItemViewHolder? {
        val inflater = LayoutInflater.from(parent.context);
        val view = inflater.inflate(R.layout.list_item_event, parent, false);
        return EventListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventListItemViewHolder, position: Int) {
        val event = events!!.get(position)
        holder.eventTitle.text = event.name
        if (event.startDate != null) {
            holder.eventTime.text = DateUtils
                    .formatDateTime(holder.itemView.context, event.startDate!!.time,
                            DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
        } else {
            holder.eventTime.text = null
        }

        val imageUri = if (event.eventImages != null && event.eventImages!!.isNotEmpty()) {
            event.eventImages!!.get(0).thumbUrl?.original
        } else {
            null
        }
        if (TextUtils.isEmpty(imageUri)) {
            holder.eventImage.setImageBitmap(null)
        } else {
            Picasso.with(holder.itemView.context).load(imageUri).into(holder.eventImage)
        }

        holder.eventListItem.setOnClickListener { view ->
            viewModel?.itemClickListener?.call(event)
        }
    }

    override fun getItemCount(): Int {
        return if (events != null) events!!.size else 0
    }
}
