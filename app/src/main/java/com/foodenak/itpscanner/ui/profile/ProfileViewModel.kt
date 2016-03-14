package com.foodenak.itpscanner.ui.profile

import android.view.View
import com.foodenak.itpscanner.entities.User
import com.foodenak.itpscanner.interactors.EditPasswordInteractor
import com.foodenak.itpscanner.interactors.EditProfileInteractor
import com.foodenak.itpscanner.interactors.GetUserInteractor
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.services.UserSession
import com.foodenak.itpscanner.services.exception.ResponseException
import com.foodenak.itpscanner.utils.TextWatcherAdapter
import com.foodenak.itpscanner.utils.applyScheduler
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/12/2015.
 */
@Singleton
class ProfileViewModel @Inject constructor(val model: UserModel) {

    var view: ProfileView? = null

    private var name: String = ""

    private var username: String = ""

    private var email: String = ""

    private var password: String = ""

    private var newPassword: String = ""

    private var confirmNewPassword: String = ""

    init {
        GetUserInteractor(model).execute(UserSession.currentSession.id)
                .applyScheduler()
                .subscribe({ user ->
                    this.user = user
                    if (user != null) {
                        view?.initializeUser(user)
                    }
                }, {})
    }

    val nameTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            name = s.toString()
            view?.hideNameErrorMessage()
        }
    }

    val usernameTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            username = s.toString()
            view?.hideUsernameErrorMessage()
        }
    }

    val emailTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            email = s.toString()
            view?.hideEmailErrorMessage()
        }
    }

    val passwordTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            password = s.toString()
            view?.hidePasswordErrorMessage()
        }
    }

    val newPasswordTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newPassword = s.toString()
            view?.hideNewPasswordErrorMessage()
        }
    }

    val confirmNewPasswordTextWatcher = object : TextWatcherAdapter() {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            confirmNewPassword = s.toString()
            view?.hideConfirmNewPasswordErrorMessage()
        }
    }

    val submitProfileButtonListener = View.OnClickListener {
        view!!.validateProfile()
    }

    val submitPasswordButtonListener = View.OnClickListener {
        view!!.validatePassword()
    }

    val profileValidationSuccessListener = Runnable {
        EditProfileInteractor(model).execute(User(
                hashId = UserSession.currentSession.id, name = name, email = email, username = username
        )).applyScheduler()
                .subscribe({ user ->
                    this.user = user
                }, { e ->
                    if (e is ResponseException) {

                    } else {

                    }
                })
    }

    val passwordValidationSuccessListener = Runnable {
        EditPasswordInteractor(model)
                .execute(EditPasswordInteractor.Argument(password, newPassword))
                .applyScheduler()
                .subscribe({ user ->
                    this.user = user
                }, { e ->
                    if (e is ResponseException) {

                    } else {

                    }
                })
    }

    val subscription: CompositeSubscription? = null

    var user: User? = null

    fun takeView(view: ProfileView) {
        if (this.view == view) {
            return
        }
        this.view = view
        if (user != null) {
            view.initializeUser(user!!)
        }
    }
}