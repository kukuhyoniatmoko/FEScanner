package com.foodenak.itpscanner.ui.redeem

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.utils.HasComponent
import com.foodenak.itpscanner.utils.obtainApplicationComponent
import kotlinx.android.synthetic.main.activity_redeem.*
import javax.inject.Inject

/**
 * Created by ITP on 10/9/2015.
 */
class RedeemActivity : AppCompatActivity(), HasComponent<Component> {

  private var component: Component? = null

  lateinit var viewModel: RedeemViewModel
    @Inject set

  private var eventId: Long? = null

  private var userId: String? = null

  private var deviceId: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_redeem)
    setSupportActionBar(toolbar)
    component().inject(this)
    bindView()
    val manager = supportFragmentManager
    val fragment = manager.findFragmentById(R.id.content_frame)
    fragment ?: manager.beginTransaction().replace(R.id.content_frame, RedeemFragment()).commit()
  }

  private fun bindView() {
    val actionBar = supportActionBar!!
    actionBar.setDisplayHomeAsUpEnabled(true)
    actionBar.setDefaultDisplayHomeAsUpEnabled(true)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        viewModel.backButtonClickListener.invoke()
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }

  override fun onBackPressed() {
    viewModel.backButtonClickListener.invoke()
  }

  override fun component(): Component {
    if (component == null) {
      synchronized(this.javaClass) {
        if (component == null) {
          eventId = intent.getLongExtra(EVENT_ID, 0)
          userId = intent.getStringExtra(USER_ID)
          deviceId = intent.getStringExtra(DEVICE_ID)
          component = createComponent(Module(eventId!!, userId!!, deviceId))
        }
      }
    }
    return component!!
  }

  private fun createComponent(module: Module): Component {
    val applicationComponent = obtainApplicationComponent()
    return DaggerComponent.builder().module(module).fEApplicationComponent(
        applicationComponent).build()
  }

  companion object {

    val USER_ID = "RedeemActivity.USER_ID"

    val EVENT_ID = "RedeemActivity.EVENT_ID"

    val DEVICE_ID = "RedeemActivity.DEVICE_ID"

    val ERROR_MESSAGE = "RedeemActivity.ERROR_MESSAGE"
  }
}