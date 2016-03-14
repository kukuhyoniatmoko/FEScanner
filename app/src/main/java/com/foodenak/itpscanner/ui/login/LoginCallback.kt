package com.foodenak.itpscanner.ui.login

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.TwitterSession

/**
 * Created by ITP on 10/5/2015.
 */
interface LoginCallback {

    fun getTwitterCredential(callback: Callback<TwitterSession>);

    fun getTwitterEmail(session: TwitterSession, callback: Callback<String>)

    fun loginWithGoogle()
}
