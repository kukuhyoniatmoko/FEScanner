package com.foodenak.itpscanner.ui.scan

import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import com.foodenak.itpscanner.entities.HistoryParameter
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.interactors.GetHistoryInteractor
import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.services.RESULT_NOT_FOUND
import com.foodenak.itpscanner.services.exception.ResponseException
import com.foodenak.itpscanner.services.exception.ResultNotFoundException
import com.foodenak.itpscanner.utils.LoadMoreDeterminer
import com.foodenak.itpscanner.utils.applyScheduler
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.functions.Action1
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class HistoryViewModel @Inject constructor(val model: EventModel) {

  private val query: HistoryParameter = HistoryParameter(0, 1)

  private var view: HistoryView? = null

  private var subscription: Subscription? = null

  fun setEventId(eventId: Long) {
    query.eventId = eventId
  }

  private var poolHistorySubscription: Subscription? = null

  fun takeView(view: HistoryView) {
    if (this.view == view) {
      return;
    }
    this.view = view;
    view.setSearchQuery(query.find)
    subscription?.unsubscribe()
    if (historyObservable != null) {
      scrollListener.isLoading = true
      subscription = historyObservable!!
          .applyScheduler()
          .subscribe(historyObserver)
    } else {
      loadHistory()
    }
    startPoolHistory()
  }

  internal fun startPoolHistory() {
    poolHistorySubscription?.unsubscribe()
    poolHistorySubscription = model.poolHistory(query.eventId)
        .retry { i, throwable -> throwable is SocketTimeoutException }
        .applyScheduler()
        .subscribe({ users ->
          view?.addHistory(0, users)
        }, {})
  }

  fun dropView(view: HistoryView) {
    if (this.view == view) {
      this.view = null;
      subscription?.unsubscribe()
      poolHistorySubscription?.unsubscribe()
      subscription = null
    }
  }

  val clearQueryButtonListener = View.OnClickListener {
    query.filterAfter = null
    query.filterBefore = null
    query.find = ""
    subscription?.unsubscribe()
    historyObservable = null
    view!!.setHistory(arrayListOf())
    view!!.setSearchQuery(query.find)
    loadHistory()
  }

  val searchButtonClickListener = View.OnClickListener {
    poolHistorySubscription?.unsubscribe()
    poolHistorySubscription = null
  }

  val searchExpandListener = object : MenuItemCompat.OnActionExpandListener {
    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
      return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
      startPoolHistory()
      query.filterAfter = null
      query.filterBefore = null
      query.page = 1
      if (TextUtils.isEmpty(query.find)) {
        return true
      }
      query.find = null
      loadHistory()
      return true
    }
  }

  val queryTextChangedListener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String?): Boolean {
      this@HistoryViewModel.query.filterAfter = null
      this@HistoryViewModel.query.filterBefore = null
      this@HistoryViewModel.query.find = query
      this@HistoryViewModel.query.page = 1
      subscription?.unsubscribe()
      historyObservable = null
      view!!.setHistory(arrayListOf())
      view!!.setSearchQuery(query)
      loadHistory()
      return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
      return false
    }
  }

  val refreshListener = SwipeRefreshLayout.OnRefreshListener {
    query.filterAfter = null
    query.filterBefore = null
    query.page = 1;
    loadHistory()
  }

  val scrollListener: LoadMoreDeterminer = LoadMoreDeterminer(Runnable {
    loadHistory()
  })

  val historyItemClickListener = Action1<User> { user ->
    view!!.navigateToOptions(userId = user.hashId!!)
  }

  fun loadHistory() {
    subscription?.unsubscribe()
    scrollListener.isLoading = true
    view!!.showProgress(true)
    if (historyObservable == null) {
      historyObservable = GetHistoryInteractor(model).execute(query).cache()
    }
    subscription = historyObservable!!.applyScheduler()
        .subscribe(historyObserver)
  }

  var historyObservable: Observable<List<User>>? = null

  val historyObserver = object : Observer<List<User>> {
    override fun onNext(t: List<User>) {
      historyObservable = null
      if (query.page <= 1) {
        query.page++
        view!!.setHistory(t)
        startPoolHistory()
        return
      }
      query.page++
      view?.addHistory(t)
    }

    override fun onError(e: Throwable?) {
      historyObservable = null
      view?.showProgress(false)
      if (e is ResultNotFoundException) {
        scrollListener.isComplete = true
        view?.showResultNotFoundMessageIfNeeded()
      } else if (e is ResponseException) {
        when (e.getStatus()) {
          RESULT_NOT_FOUND -> {
            scrollListener.isComplete = true
            view?.showResultNotFoundMessageIfNeeded()
          }
          else -> view?.showCantFetchHistoryMessage()
        }
      } else {
        view?.showCantFetchHistoryMessage()
      }
      view?.hideRefreshIndicator()
      scrollListener.isLoading = false
    }

    override fun onCompleted() {
      historyObservable = null
      view?.hideRefreshIndicator()
      view?.showProgress(false)
      scrollListener.isLoading = false
    }
  }
}