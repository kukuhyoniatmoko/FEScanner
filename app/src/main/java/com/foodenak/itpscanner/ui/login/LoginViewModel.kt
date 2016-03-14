package com.foodenak.itpscanner.ui.login

import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.facebook.AccessToken
import com.foodenak.itpscanner.entities.FacebookCredential
import com.foodenak.itpscanner.entities.GoogleCredential
import com.foodenak.itpscanner.entities.TwitterCredential
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.interactors.*
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.services.INVALID_CREDENTIAL
import com.foodenak.itpscanner.services.InvalidAdminCredentialException
import com.foodenak.itpscanner.services.exception.ResponseException
import com.foodenak.itpscanner.utils.TextWatcherAdapter
import com.foodenak.itpscanner.utils.applyScheduler
import com.twitter.sdk.android.core.TwitterSession
import rx.Observer
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/5/2015.
 */
@Singleton
class LoginViewModel @Inject constructor(val model: UserModel) {

    var view: LoginView? = null;

    var username: String = "";

    var password: String = "";

    var subscription: CompositeSubscription? = null;

    val loginObserver: Observer<User> = object : Observer<User> {
        override fun onError(e: Throwable?) {
            view?.hideProgressIndicator()
            if (e is InvalidAdminCredentialException) {
                view?.showInvalidAdminMessage()
            } else if (e is ResponseException && e.getStatus() == INVALID_CREDENTIAL) {
                view?.showInvalidCredentialMessage()
            } else {
                view?.showUnknownErrorMessage()
            }
        }

        override fun onNext(t: User) {
            view!!.loginSuccess(t)
        }

        override fun onCompleted() {
            view?.hideProgressIndicator()
        }
    }

    val facebookLoginClickListener = View.OnClickListener { view!!.requestFacebookCredential() }

    val twitterLoginClickListener = View.OnClickListener { view!!.requestTwitterCredential() }

    val googleLoginClickListener = View.OnClickListener { view!!.requestGoogleCredential() }

    val loginClickListener = View.OnClickListener { view ->
        performLogin()
    }

    private fun performLogin() {
        if (username.length < 4 || username.length > 60) {
            this.view!!.showInvalidUsernameMessage();
            return
        }
        if (password.length < 4 || password.length > 60) {
            this.view!!.showInvalidPasswordMessage();
            return
        }
        view!!.showProgressIndicator()
        val user = User()
        user.username = username;
        user.password = password;
        subscription!!.add(LoginInteractor(model).execute(user)
                .applyScheduler()
                .subscribe(loginObserver))
    }

    val editorActionListener = TextView.OnEditorActionListener { textView, i, keyEvent ->
        if (i == EditorInfo.IME_ACTION_GO) {
            performLogin()
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    val usernameTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            username = s.toString();
        }
    }

    val passwordTextWatcher: TextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            password = s.toString();
        }
    }

    fun takeView(view: LoginView) {
        if (this.view == view) {
            return;
        }
        if (subscription != null) {
            subscription!!.unsubscribe();
        }
        subscription = CompositeSubscription();
        this.view = view;
    }

    fun dropView(view: LoginView) {
        if (this.view == view) {
            this.view = null;
            if (subscription != null) {
                subscription!!.unsubscribe();
                subscription = null;
            }
        }
    }

    fun loginWithFacebook(credential: FacebookCredential) {
        subscription!!.add(LoginWithFacebookInteractor(model).execute(credential)
                .applyScheduler()
                .subscribe(loginObserver))
    }

    fun loginWithGoogle(credential: GoogleCredential) {
        subscription!!.add(LoginWithGoogleInteractor(model).execute(credential)
                .applyScheduler()
                .subscribe(loginObserver))
    }

    fun loginWithTwitter(credential: TwitterCredential) {
        subscription!!.add(LoginWithTwitterInteractor(model).execute(credential)
                .applyScheduler()
                .subscribe(loginObserver))
    }

    fun login(accessToken: AccessToken) {
        view!!.showProgressIndicator()
        subscription!!.add(GetFacebookCredential().execute(accessToken)
                .subscribeOn(Schedulers.io())
                .subscribe({ credential -> loginWithFacebook(credential) }, { error -> view!!.showUnknownErrorMessage() }))
    }

    fun login(session: TwitterSession, email: String?) {
        view!!.showProgressIndicator()
        subscription!!.add(GetTwitterCredential().execute(GetTwitterCredential.Argument(session, email))
                .subscribeOn(Schedulers.io())
                .subscribe({ credential -> loginWithTwitter(credential) }, { error -> view!!.showUnknownErrorMessage() }))
    }

    fun login(idToken: String) {
        view!!.showProgressIndicator()
        loginWithGoogle(GoogleCredential(idToken))
    }
}