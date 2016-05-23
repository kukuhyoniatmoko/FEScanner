package com.foodenak.itpscanner.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.foodenak.foodenak.ui.fragments.ShowChangeServerLongClickListener
import com.foodenak.itpscanner.R
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.ui.AlertDialogFragment
import com.foodenak.itpscanner.utils.obtainActivityComponent
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login_with_progress.*
import java.util.*
import javax.inject.Inject

/**
 * Created by ITP on 10/5/2015.
 */
class LoginFragment : Fragment(), LoginView, FacebookCallback<LoginResult> {

  @Inject lateinit var viewModel: LoginViewModel
  @Inject lateinit var loginManager: LoginManager
  @Inject lateinit var loginCallback: LoginCallback
  @Inject lateinit var googleApiClient: GoogleApiClient

  val callbackManager: CallbackManager = CallbackManager.Factory.create()
  val twitterLoginCallback = TwitterCallback()
  val emailTwitterCallback = TwitterEmailCallback()

  private var pendingLoginWithFacebook: Boolean = false
  private var pendingLoginWithTwitter: Boolean = false
  private var pendingLoginWithGoogle: Boolean = false
  private var twitterEmail: String? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val component = activity.obtainActivityComponent<Component>();
    component.inject(this)
    loginManager.registerCallback(callbackManager, this);
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_login_with_progress, container, false);
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    bindView()
  }

  private fun bindView() {
    facebook_login_button.setCompoundDrawablesRelativeWithIntrinsicBounds(
        IconDrawable(context, Iconify.IconValue.zmdi_facebook_box)
            .actionBarSize().colorRes(R.color.text_white_primary), null, null, null)
    twitter_login_button.setCompoundDrawablesRelativeWithIntrinsicBounds(
        IconDrawable(context, Iconify.IconValue.zmdi_twitter)
            .actionBarSize().colorRes(R.color.text_white_primary), null, null, null)
    facebook_login_button.setOnClickListener(viewModel.facebookLoginClickListener)
    twitter_login_button.setOnClickListener(viewModel.twitterLoginClickListener)
    google_login_button.setOnClickListener(viewModel.googleLoginClickListener)
    username.addTextChangedListener(viewModel.usernameTextWatcher)
    password.addTextChangedListener(viewModel.passwordTextWatcher)
    password.setImeActionLabel(getText(R.string.login), EditorInfo.IME_ACTION_GO)
    password.setOnEditorActionListener(viewModel.editorActionListener)
    login_button.setOnClickListener(viewModel.loginClickListener)
    login_button.setOnLongClickListener(ShowChangeServerLongClickListener(childFragmentManager))
  }

  override fun onStart() {
    super.onStart()
    viewModel.takeView(this)
  }

  override fun onResume() {
    super.onResume()
    if (pendingLoginWithGoogle && account != null) {
      pendingLoginWithGoogle = false
      viewModel.login(account!!.idToken!!)
      account = null
    } else if (pendingLoginWithFacebook) {
      pendingLoginWithFacebook = false
      viewModel.login(AccessToken.getCurrentAccessToken());
    } else if (pendingLoginWithTwitter) {
      pendingLoginWithTwitter = false
      viewModel.login(TwitterCore.getInstance().sessionManager.activeSession, twitterEmail)
    }
  }

  var account: GoogleSignInAccount? = null;

  fun loginWithGoogle(account: GoogleSignInAccount) {
    if (isResumed) {
      pendingLoginWithGoogle = false
      viewModel.login(account.idToken!!)
    } else {
      pendingLoginWithGoogle = true
      this.account = account;
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  override fun requestFacebookCredential() {
    loginManager.logInWithReadPermissions(this, READ_PERMISSIONS);
  }

  override fun onError(error: FacebookException?) {
    Toast.makeText(context, R.string.fb_login_failed, Toast.LENGTH_SHORT).show()
  }

  override fun onCancel() {
  }

  override fun onSuccess(result: LoginResult) {
    pendingLoginWithFacebook = true;
  }

  override fun requestGoogleCredential() {
    loginCallback.loginWithGoogle()
  }

  override fun requestTwitterCredential() {
    loginCallback.getTwitterCredential(twitterLoginCallback);
  }

  override fun showInvalidUsernameMessage() {
    activity.username.error = getString(R.string.max60_min5);
    activity.username.requestFocus();
  }

  override fun showInvalidPasswordMessage() {
    activity.password.error = getString(R.string.max60_min5);
    activity.password.requestFocus();
  }

  override fun showInvalidAdminMessage() {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(getString(R.string.invalid_admin_credential)).show(manager,
        ERROR_DIALOG_TAG)
    Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun showUnknownErrorMessage() {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(getString(R.string.something_went_wrong)).show(manager,
        ERROR_DIALOG_TAG)
    Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun showInvalidCredentialMessage() {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(getString(R.string.invalid_credential)).show(manager,
        ERROR_DIALOG_TAG)
    Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun showConnectionTimeoutMessage() {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(getString(R.string.register_redeem_connection_timeout)).show(
        manager,
        ERROR_DIALOG_TAG)
    Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun showNoInternetMessage() {
    val manager = childFragmentManager
    (manager.findFragmentByTag(ERROR_DIALOG_TAG) as DialogFragment?)?.dismiss()
    AlertDialogFragment.newInstance(getString(R.string.register_redeem_no_internet)).show(manager,
        ERROR_DIALOG_TAG)
    Auth.GoogleSignInApi.signOut(googleApiClient)
  }

  override fun showProgressIndicator() {
    loginView.visibility = View.GONE
    progressBar.visibility = View.VISIBLE
  }

  override fun hideProgressIndicator() {
    loginView.visibility = View.VISIBLE
    progressBar.visibility = View.GONE
  }

  override fun loginSuccess(user: User) {
    activity.setResult(Activity.RESULT_OK)
    activity.supportFinishAfterTransition()
  }

  override fun onStop() {
    super.onStop()
    viewModel.dropView(this)
  }

  companion object {
    internal const val ERROR_DIALOG_TAG = "ERROR_DIALOG_TAG"
    private val READ_PERMISSIONS = Arrays.asList("public_profile", "email")
  }

  inner class TwitterCallback : Callback<TwitterSession>() {
    override fun success(p0: Result<TwitterSession>) {
      loginCallback.getTwitterEmail(p0.data, emailTwitterCallback);
    }

    override fun failure(p0: TwitterException?) {
      Toast.makeText(context, R.string.tw_login_failed, Toast.LENGTH_SHORT).show()
    }
  }

  inner class TwitterEmailCallback : Callback<String>() {
    override fun failure(p0: TwitterException?) {
      pendingLoginWithTwitter = true;
    }

    override fun success(p0: Result<String>) {
      pendingLoginWithTwitter = true;
    }
  }
}