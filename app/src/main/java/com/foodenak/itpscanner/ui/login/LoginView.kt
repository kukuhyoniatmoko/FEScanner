package com.foodenak.itpscanner.ui.login

import com.foodenak.itpscanner.entities.User

/**
 * Created by ITP on 10/5/2015.
 */
interface LoginView {

    fun requestFacebookCredential();

    fun requestGoogleCredential();

    fun requestTwitterCredential();

    fun showInvalidUsernameMessage()

    fun showInvalidPasswordMessage()

    fun loginSuccess(user: User)

    fun showInvalidAdminMessage()

    fun showUnknownErrorMessage()

    fun showInvalidCredentialMessage()

    fun showProgressIndicator()

    fun hideProgressIndicator()
}