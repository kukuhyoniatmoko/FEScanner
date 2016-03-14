package com.foodenak.itpscanner.ui.scan

import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/8/2015.
 */
interface HistoryView {
    fun setHistory(users: List<User>)

    fun hideRefreshIndicator()

    fun showCantFetchHistoryMessage()

    fun addHistory(position: Int, users: List<User>)

    fun addHistory(users: List<User>)

    fun showProgress(visibility: Boolean)

    fun navigateToOptions(userId: String, deviceId: String? = null)

    fun showResultNotFoundMessageIfNeeded()

    fun setSearchQuery(find: String?)
}