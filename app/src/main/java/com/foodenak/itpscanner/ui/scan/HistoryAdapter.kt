package com.foodenak.itpscanner.ui.scan

import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.services.image.ImageLoader
import rx.functions.Action1
import java.util.*

/**
 * Created by ITP on 10/10/2015.
 */
class HistoryAdapter(val imageLoader: ImageLoader) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val TYPE_ITEM = 1

    val TYPE_PROGRESS = 2

    val TYPE_NOT_FOUND = 3;

    var showProgress = false

    var showResultNotFound = false

    var items: MutableList<User>? = null

    var clickListener: Action1<User>? = null

    var clearQueryListener: View.OnClickListener? = null

    override fun getItemCount(): Int {
        val count = if (items == null) 0 else items!!.size
        return if (hasFooter()) count + 1 else count
    }

    fun setHistory(users: List<User>) {
        items = users.toMutableList()
        notifyDataSetChanged()
    }

    fun addHistory(users: List<User>) {
        val index = if (items == null) 0 else items!!.size
        addHistory(index, users)
    }

    fun addHistory(index: Int, users: List<User>) {
        val count = users.size
        if (items == null) {
            items = ArrayList<User>()
        }
        items!!.addAll(index, users)
        notifyItemRangeInserted(index, count)
    }

    fun isEmpty(): Boolean {
        val items = this.items
        return items == null || items.isEmpty()
    }

    fun setFooter(type: Int, show: Boolean, resetOther: Boolean) {
        val hasFooter = hasFooter()
        if (!hasFooter && !show) {
            return
        }
        when (type) {
            FOOTER_LOADING -> {
                if (show || resetOther) {
                    showResultNotFound = false
                }
                showProgress = show
            }
            FOOTER_NOT_FOUND -> {
                if (show || resetOther) {
                    showProgress = false
                }
                showResultNotFound = show
            }
        }
        if (hasFooter && show) {
            notifyItemChanged(itemCount - 1)
        } else if (!show) {
            notifyItemRemoved(itemCount)
        } else {
            notifyItemInserted(itemCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_ITEM ->
                return HistoryListItemHolder(inflater.inflate(R.layout.list_item_history, parent, false))
            TYPE_PROGRESS ->
                return ProgressListItemHolder(inflater.inflate(R.layout.list_item_history_progress, parent, false))
            TYPE_NOT_FOUND ->
                return ResultNotFoundHolder(inflater.inflate(R.layout.list_item_result_not_found, parent, false))
            else ->
                throw IllegalArgumentException("Invalid viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (holder) {
            is HistoryListItemHolder -> {
                val user = items!!.get(position)
                holder.usernameTextView.text = "@${user.username}"
                holder.nameTextView.text = user.name
                if (TextUtils.isEmpty(user.thumbUrl?.original)) {
                    holder.profilePhoto.setImageDrawable(null)
                } else {
                    imageLoader.load(user.thumbUrl!!.original!!, holder.profilePhoto)
                }
                val pivot = user.pivot
                var date: Date? = null
                if (pivot != null) if (pivot.redeemLuckydipAt != null || pivot.redeemVoucherAt != null) {
                    if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt == null) {
                        date = pivot.redeemLuckydipAt
                        holder.redeemTextView.setText(R.string.lucky_dip_redeemed)
                    } else if (pivot.redeemLuckydipAt == null && pivot.redeemVoucherAt != null) {
                        date = pivot.redeemVoucherAt
                        holder.redeemTextView.setText(R.string.voucher_redeemed)
                    } else if (pivot.redeemLuckydipAt != null && pivot.redeemVoucherAt != null) {
                        date = Date(Math.max(pivot.redeemLuckydipAt!!.time, pivot.redeemVoucherAt!!.time))
                        holder.redeemTextView.setText(R.string.lucky_dip_voucher_redeemed)
                    }
                }
                if (date != null) holder.dateTextView.text = DateUtils
                        .formatDateTime(holder.itemView.context, date.time,
                                DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
                holder.itemView.setOnClickListener(View.OnClickListener {
                    clickListener?.call(user)
                })
            }
            is ResultNotFoundHolder -> {
                if (TextUtils.isEmpty(searchQuery)) {
                    holder.queryTextView.visibility = View.GONE
                    holder.clearQueryButton.visibility = View.GONE
                    holder.clearQueryButton.setOnClickListener(null)
                } else {
                    holder.queryTextView.visibility = View.VISIBLE
                    holder.clearQueryButton.visibility = View.VISIBLE
                    holder.queryTextView.text = "Query: $searchQuery"
                    holder.clearQueryButton.setOnClickListener(clearQueryListener)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (hasFooter() && position == itemCount - 1) {
            if (showProgress) return TYPE_PROGRESS
            if (showResultNotFound) return TYPE_NOT_FOUND
        }
        return TYPE_ITEM
    }

    private fun hasFooter(): Boolean {
        return showResultNotFound || showProgress
    }

    companion object {

        const val FOOTER_LOADING = 1

        const val FOOTER_NOT_FOUND = 2
    }

    var searchQuery: String? = null
}