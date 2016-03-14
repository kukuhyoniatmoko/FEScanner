package com.foodenak.itpscanner.ui.login

import com.facebook.CallbackManager
import com.foodenak.itpscanner.utils.ActivityScope
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import dagger.Module
import dagger.Provides

/**
 * Created by ITP on 10/5/2015.
 */
@Module class Module(val activity: LoginActivity) {

  @Provides @ActivityScope internal fun provideLoginCallback(): LoginCallback = activity

  @Provides @ActivityScope internal fun provideCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }

  @Provides @ActivityScope internal fun provideGoogleApiClient(): GoogleApiClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken("233749399763-iolj614o0ichjit2ubfclql2f7t3e4be.apps.googleusercontent.com")
                .build();
        return GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, activity.connectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(activity)
                .build();
    }
}