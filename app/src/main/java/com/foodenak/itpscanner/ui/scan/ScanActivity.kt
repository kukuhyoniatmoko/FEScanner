package com.foodenak.itpscanner.ui.scan

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.view.MenuItemCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.services.image.ImageLoader
import com.foodenak.itpscanner.ui.events.EventsActivity
import com.foodenak.itpscanner.ui.login.LoginActivity
import com.foodenak.itpscanner.utils.HasComponent
import com.foodenak.itpscanner.utils.obtainApplicationComponent
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import kotlinx.android.synthetic.main.activity_scan.tapLayout
import kotlinx.android.synthetic.main.activity_scan.toolbar
import kotlinx.android.synthetic.main.activity_scan.viewPager
import kotlinx.android.synthetic.main.activity_scan_with_nav.drawerLayout
import kotlinx.android.synthetic.main.activity_scan_with_nav.navigationView
import javax.inject.Inject

/**
 * Created by ITP on 10/6/2015.
 */
class ScanActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    ScanFragment.Callback,
    HasComponent<Component> {

  @Inject lateinit var imageLoader: ImageLoader

  @Inject lateinit var historyViewModel: HistoryViewModel

  private var component: Component? = null

  private var adapter: ScanPagerAdapter? = null

  private var eventName: String = ""

  private var eventId: Long = 0

  private var navPhoto: ImageView? = null

  private var navName: TextView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.ScanActivityTheme)
    super.onCreate(savedInstanceState)
    component().inject(this)
    setContentView(R.layout.activity_scan_with_nav)
    setSupportActionBar(toolbar)
    supportActionBar!!.title = eventName
    bindView();
  }

  private fun bindView() {
    adapter = ScanPagerAdapter(this, supportFragmentManager)
    viewPager.adapter = adapter
    tapLayout.setupWithViewPager(viewPager)
    viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
      override fun onPageScrollStateChanged(state: Int) {

      }

      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

      }

      override fun onPageSelected(position: Int) {
        val configuration = resources.configuration
        val orientation = configuration.orientation
        val portrait = orientation == Configuration.ORIENTATION_PORTRAIT
        if (portrait) {
          supportInvalidateOptionsMenu()
        }
      }
    })
    val actionBar = supportActionBar
    actionBar?.setDisplayHomeAsUpEnabled(true)
    actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
    val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
    val headerView = navigationView.inflateHeaderView(R.layout.view_navigation_header)
    navPhoto = headerView.findViewById(R.id.navPhoto) as ImageView?
    navName = headerView.findViewById(R.id.navName) as TextView?
    drawerLayout.setDrawerListener(drawerToggle)
    drawerToggle.syncState()
    navigationView.setNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.nav_logout -> {
          val manager = supportFragmentManager
          (manager.findFragmentByTag(CONFIRM_LOGOUT_TAG) as DialogFragment?)?.dismiss()
          ScanLogoutConfirmationDialog().show(manager, CONFIRM_LOGOUT_TAG)
          return@setNavigationItemSelectedListener true
        }
      }
      false
    }
  }

  var voucherCount = 0

  override fun setVoucherRemaining(count: Int) {
    voucherCount = count
    supportInvalidateOptionsMenu()
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val configuration = resources.configuration
    val orientation = configuration.orientation
    val landscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    val inflater = menuInflater
    if (landscape) {
      inflater.inflate(R.menu.menu_scan_history, menu)
    } else {
      if (viewPager.currentItem == 1) {
        inflater.inflate(R.menu.menu_history, menu)
      } else if (viewPager.currentItem == 0) {
        inflater.inflate(R.menu.menu_scan, menu)
      }
    }
    if (viewPager.currentItem == 1 || landscape) {
      val menuItem = menu.findItem(R.id.action_search)
      menuItem.icon = IconDrawable(this, Iconify.IconValue.zmdi_search).actionBarSize().colorRes(R.color.text_white_primary)
      val searchView = MenuItemCompat.getActionView(menuItem) as SearchView?
      if (searchView != null) {
        searchView.setOnSearchClickListener(historyViewModel.searchButtonClickListener)
        searchView.setOnQueryTextListener(historyViewModel.queryTextChangedListener)
        searchView.queryHint = getString(R.string.search_history)
      }
      MenuItemCompat.setOnActionExpandListener(menuItem, historyViewModel.searchExpandListener)
    }
    if (viewPager.currentItem == 0 || landscape) {
      val menuItem = menu.findItem(R.id.action_voucher_remaining)
      MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS)
      val inf = LayoutInflater.from(this)
      val textView = inf.inflate(R.layout.view_voucher_remaining, null) as TextView
      textView.text = voucherCount.toString()
      textView.setCompoundDrawablesWithIntrinsicBounds(
          null, null,
          IconDrawable(this, Iconify.IconValue.zmdi_card_giftcard)
              .actionBarSize()
              .colorRes(R.color.text_white_primary)
          , null)
      MenuItemCompat.setActionView(menuItem, textView)
    }
    return true
  }

  override fun onResume() {
    super.onResume()
    if (!UserSession.currentSession.isActive()) {
      requestLogin()
      return
    }
    val preferences = PreferenceManager.getDefaultSharedPreferences(this)
    eventId = preferences.getLong(EXTRA_EVENT_ID, 0)
    eventName = preferences.getString(EXTRA_EVENT_NAME, "")
    if (eventId == 0.toLong()) {
      requestEvent()
      return
    }
    supportActionBar?.title = eventName
    if (UserSession.currentSession.isActive() && UserSession.currentSession.user != null) {
      val user = UserSession.currentSession.user
      if (user?.thumbUrl?.original == null) {
        navPhoto!!.setImageDrawable(null)
      } else {
        imageLoader.load(user!!.thumbUrl!!.original!!, navPhoto!!)
      }
      navName!!.text = user?.name
    }
  }

  private fun requestEvent() {
    val intent = Intent(this, EventsActivity::class.java)
    startActivityForResult(intent, EVENT_REQUEST)
  }

  private fun requestLogin() {
    val intent = Intent(this, LoginActivity::class.java)
    intent.putExtra(LoginActivity.SHOULD_LOGOUT, true)
    startActivityForResult(intent, LOGIN_REQUEST)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      LOGIN_REQUEST -> {
        if (resultCode != Activity.RESULT_OK) {
          finish()
        }
        return
      }
      EVENT_REQUEST -> {
        if (intent != null && intent.getBooleanExtra(EventsActivity.RESULT_SHOULD_LOGOUT, false)
            || !UserSession.currentSession.isActive()) {
          return
        }
        if (resultCode != Activity.RESULT_OK) {
          finish()
        } else {
          val preferences = PreferenceManager.getDefaultSharedPreferences(this)
          eventId = preferences.getLong(EXTRA_EVENT_ID, 0)
          eventName = preferences.getString(EXTRA_EVENT_NAME, "")
        }
        return
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  override fun component(): Component {
    synchronized(this.javaClass) {
      if (component == null) {
        component = createComponent(Module(this))
      }
    }
    return component!!;
  }

  internal fun createComponent(module: Module): Component {
    val applicationComponent = obtainApplicationComponent()
    return DaggerComponent.builder().fEApplicationComponent(applicationComponent).module(module).build()
  }

  companion object {

    val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    val PERMISSIONS_REQUEST = 1;

    val EVENT_REQUEST = 2;

    val LOGIN_REQUEST = 3;

    val RATIONAL_FRAGMENT_TAG = "RATIONAL_FRAGMENT_TAG"

    val CONFIRM_LOGOUT_TAG = "CONFIRM_LOGOUT_TAG"

    val EXTRA_EVENT_NAME = "EXTRA_EVENT_NAME"

    val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
  }
}