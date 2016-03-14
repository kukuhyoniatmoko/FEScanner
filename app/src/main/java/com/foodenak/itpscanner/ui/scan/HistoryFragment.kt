package com.foodenak.itpscanner.ui.scan

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.services.image.ImageLoader
import com.foodenak.itpscanner.ui.events.LinearDivider
import com.foodenak.itpscanner.ui.redeem.RedeemActivity
import com.foodenak.itpscanner.utils.obtainActivityComponent
import kotlinx.android.synthetic.main.fragment_history.recyclerView
import kotlinx.android.synthetic.main.fragment_history.refreshLayout
import javax.inject.Inject

/**
 * Created by ITP on 10/7/2015.
 */
class HistoryFragment : Fragment(), HistoryView {

    @Inject lateinit var viewModel: HistoryViewModel

    @Inject lateinit var imageLoader: ImageLoader

    lateinit var adapter: HistoryAdapter

    var eventId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity.obtainActivityComponent<Component>().inject(this)
        adapter = HistoryAdapter(imageLoader)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    private fun bindView() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(LinearDivider(context, R.drawable.recycler_divider_1_dp))
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(viewModel.scrollListener)
        refreshLayout.setColorSchemeResources(R.color.red_foodenak)
        refreshLayout.setOnRefreshListener(viewModel.refreshListener)
        adapter.clickListener = viewModel.historyItemClickListener
        adapter.clearQueryListener = viewModel.clearQueryButtonListener
    }

    override fun onStart() {
        super.onStart()
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        eventId = preferences.getLong(ScanActivity.EXTRA_EVENT_ID, 0)
        if (UserSession.currentSession.isActive() && eventId != 0.toLong()) {
            viewModel.setEventId(eventId)
            viewModel.takeView(this)
        }
    }

    override fun setHistory(users: List<User>) {
        adapter.setHistory(users)
    }

    override fun addHistory(position: Int, users: List<User>) {
        adapter.addHistory(position, users)
    }

    override fun addHistory(users: List<User>) {
        adapter.addHistory(users)
    }

    override fun showProgress(visibility: Boolean) {
        adapter.setFooter(HistoryAdapter.FOOTER_LOADING, visibility, true)
    }

    override fun showResultNotFoundMessageIfNeeded() {
        if (adapter.isEmpty()) {
            adapter.setFooter(HistoryAdapter.FOOTER_NOT_FOUND, true, true)
        }
    }

    override fun setSearchQuery(find: String?) {
        adapter.searchQuery = (find)
    }

    override fun hideRefreshIndicator() {
        refreshLayout.isRefreshing = false
    }

    override fun showCantFetchHistoryMessage() {
        Toast.makeText(context, R.string.cant_refresh_history, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToOptions(userId: String, deviceId: String?) {
        val intent = Intent(context, RedeemActivity::class.java)
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        eventId = preferences.getLong(ScanActivity.EXTRA_EVENT_ID, 0)
        intent.putExtra(RedeemActivity.USER_ID, userId)
        intent.putExtra(RedeemActivity.DEVICE_ID, deviceId)
        intent.putExtra(RedeemActivity.EVENT_ID, eventId)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        viewModel.dropView(this)
    }
}