package com.foodenak.itpscanner.ui.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.utils.HasComponent
import com.foodenak.itpscanner.utils.obtainApplicationComponent
import kotlinx.android.synthetic.main.activty_events.toolbar

/**
 * Created by ITP on 10/7/2015.
 */
class EventsActivity : AppCompatActivity(), HasComponent<Component> {

  private var component: Component? = null;

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    component().inject(this)
    setContentView(R.layout.activty_events)
    toolbar.setTitle(R.string.select_event)
    setSupportActionBar(toolbar)

    val manager = supportFragmentManager
    val fragment = manager.findFragmentById(R.id.content_frame)
    if (fragment == null) {
      manager.beginTransaction().replace(R.id.content_frame, EventsFragment()).commit()
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.menu_select_events, menu)
    return true;
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.action_logout -> {
        showLogoutConfirmation()
        return true;
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun showLogoutConfirmation() {
    val manager = supportFragmentManager
    (manager.findFragmentByTag(CONFIRM_LOGOUT_TAG) as DialogFragment?)?.dismiss()
    EventLogoutConfirmationDialog().show(manager, CONFIRM_LOGOUT_TAG)
  }

  override fun onResume() {
    super.onResume()
    if (!UserSession.currentSession.isActive()) {
      val intent = Intent()
      intent.putExtra(RESULT_SHOULD_LOGOUT, true)
      setResult(Activity.RESULT_CANCELED, intent)
      ActivityCompat.finishAfterTransition(this)
    }
  }

  override fun component(): Component {
    synchronized(this.javaClass) {
      if (component == null) {
        component = createComponent()
      }
    }
    return component!!
  }

  private fun createComponent(): Component {
    val applicationComponent = obtainApplicationComponent()
    return DaggerComponent.builder().fEApplicationComponent(applicationComponent).build()
  }

  companion object {

    val RESULT_SHOULD_LOGOUT = "RESULT_SHOULD_LOGOUT"

    val CONFIRM_LOGOUT_TAG = "CONFIRM_LOGOUT_TAG";
  }
}