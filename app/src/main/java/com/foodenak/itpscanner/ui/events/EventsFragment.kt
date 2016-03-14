package com.foodenak.itpscanner.ui.events

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.ui.scan.ScanActivity
import com.foodenak.itpscanner.utils.obtainActivityComponent
import kotlinx.android.synthetic.main.fragment_events.*
import javax.inject.Inject

/**
 * Created by ITP on 10/7/2015.
 */
open class EventsFragment : Fragment(), EventsView {

    var eventsAdapter: EventsAdapter = EventsAdapter();

    var viewModel: EventsViewModel? = null
        @Inject set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val component: Component = activity.obtainActivityComponent()
        component.inject(this)
        eventsAdapter.viewModel = viewModel
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    protected open fun bindView() {
        val configuration = resources.configuration
        val orientation = configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            val spacing = resources.getDimensionPixelSize(R.dimen.grid_divider)
            recyclerView.addItemDecoration(GridDivider(2, spacing, true))
        } else {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.addItemDecoration(LinearDivider(context, R.drawable.recycler_divider_16_dp))
        }
        recyclerView.adapter = eventsAdapter
        refreshLayout.setColorSchemeResources(R.color.red_foodenak)
        refreshLayout.setOnRefreshListener(viewModel!!.refreshListener)
    }

    override fun onStart() {
        super.onStart()
        if (UserSession.currentSession.isActive()) viewModel!!.takeView(this)
    }

    override fun setEvents(data: List<Event>?) {
        eventsAdapter.setItems(data)
    }

    override fun showErrorMessage() {
        Toast.makeText(context, R.string.something_went_wrong, Toast.LENGTH_LONG).show()
    }

    override fun finishRefresh() {
        activity.refreshLayout.isRefreshing = false
    }

    override fun showSelectEventConfirmation(event: Event) {
        val manager = childFragmentManager
        (manager.findFragmentByTag(SELECT_EVENT_CONFIRMATION_TAG) as DialogFragment?)?.dismiss()
        SelectEventConfirmationDialog().show(manager, SELECT_EVENT_CONFIRMATION_TAG)
    }

    override fun notifyEventSelected(event: Event) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        preferences.edit()
                .putLong(ScanActivity.EXTRA_EVENT_ID, event.id!!)
                .putString(ScanActivity.EXTRA_EVENT_NAME, event.name)
                .commit()
        val intent = Intent()
        intent.putExtra(EventsActivity.RESULT_SHOULD_LOGOUT, false)
        activity.setResult(Activity.RESULT_OK, intent)
        ActivityCompat.finishAfterTransition(activity)
    }

    override fun onStop() {
        super.onStop()
        viewModel!!.dropView(this)
    }

    companion object {

        val SELECT_EVENT_CONFIRMATION_TAG = "SELECT_EVENT_CONFIRMATION_TAG"
    }
}