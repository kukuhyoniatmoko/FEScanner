package com.foodenak.itpscanner.ui.login

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.facebook.login.LoginManager
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.ui.scan.ScanActivity
import com.foodenak.itpscanner.utils.HasComponent
import com.foodenak.itpscanner.utils.obtainApplicationComponent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import kotlinx.android.synthetic.main.activty_login.*
import javax.inject.Inject

/**
 * Created by ITP on 10/5/2015.
 */
class LoginActivity : AppCompatActivity(), LoginCallback, GoogleApiClient.ConnectionCallbacks, HasComponent<Component> {

  @Inject lateinit var twitterAuthClient: TwitterAuthClient
  @Inject lateinit var googleApiClient: GoogleApiClient
  @Inject lateinit var loginManager: LoginManager

  private var component: Component? = null
  val connectionFailedListener: GoogleApiClient.OnConnectionFailedListener = ConnectionFailedListener()
  private var signInProgress: Int = STATE_DEFAULT
  private var shouldLogout: Boolean = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    shouldLogout = intent.getBooleanExtra(SHOULD_LOGOUT, false)
    setContentView(R.layout.activty_login)
    setSupportActionBar(toolbar)

    component().inject(this)

    val manager = supportFragmentManager;
    val fragment = manager.findFragmentById(R.id.content_frame);
    if (fragment == null) {
      manager.beginTransaction()
          .replace(R.id.content_frame, LoginFragment())
          .commit();
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    signInProgress = savedInstanceState.getInt(STATE_SIGN_IN_PROGRESS)
    shouldLogout = savedInstanceState.getBoolean(SHOULD_LOGOUT, false)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt(STATE_SIGN_IN_PROGRESS, signInProgress)
    outState.putBoolean(SHOULD_LOGOUT, shouldLogout)
    super.onSaveInstanceState(outState)
  }

  override fun onStart() {
    super.onStart()
    Log.i("FacebookLogin", "activity onStart shouldLogout = $shouldLogout")
    if (shouldLogout) {
      shouldLogout = false
      loginManager.logOut()
      googleApiClient.connect()
      TwitterCore.getInstance().sessionManager.clearActiveSession()
      val preferences = PreferenceManager.getDefaultSharedPreferences(this)
      preferences.edit()
          .putLong(ScanActivity.EXTRA_EVENT_ID, 0)
          .putString(ScanActivity.EXTRA_EVENT_NAME, "")
          .apply()
    }
  }

  override fun onConnected(p0: Bundle?) {
    if (shouldLogout) Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun onConnectionSuspended(p0: Int) {

  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data);
    twitterAuthClient.onActivityResult(requestCode, resultCode, data);
    when (requestCode) {
      RC_SIGN_IN -> {
        val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess) {
          val fragment = supportFragmentManager.findFragmentById(R.id.content_frame)!!

          if (fragment is LoginFragment) fragment.loginWithGoogle(result.signInAccount!!)
        } else {
          Log.e("Login failed", "result = ${result.status}")
          Toast.makeText(this, R.string.login_with_google_failed, Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  override fun getTwitterCredential(callback: Callback<TwitterSession>) {
    twitterAuthClient.authorize(this, callback);
  }

  override fun getTwitterEmail(session: TwitterSession, callback: Callback<String>) {
    twitterAuthClient.requestEmail(session, callback);
  }

  override fun loginWithGoogle() {
    val intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
    startActivityForResult(intent, RC_SIGN_IN)
  }

  inner class ConnectionFailedListener : GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
      Log.e("Login failed", "onConnectionFailed: $p0")
    }
  }

  override fun onStop() {
    super.onStop()
    googleApiClient.disconnect()
  }

  override fun component(): Component {
    synchronized(this.javaClass) {
      if (component == null) {
        component = createComponent(Module(this));
      }
    }
    return component!!;
  }

  private fun createComponent(module: Module): Component {
    val applicationComponent = obtainApplicationComponent()
    return DaggerComponent.builder().fEApplicationComponent(applicationComponent).module(
        module).build()
  }

  companion object {
    const val SHOULD_LOGOUT = "SHOULD_LOGOUT"
    const val STATE_DEFAULT = 0
    const val RC_SIGN_IN = 9872
    private const val STATE_SIGN_IN_PROGRESS = "STATE_SIGN_IN_PROGRESS"
  }
}
