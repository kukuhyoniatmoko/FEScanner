package com.foodenak.itpscanner

import android.content.Context
import com.facebook.login.LoginManager
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by ITP on 10/5/2015.
 */
@Module class FEApplicationModule(val context: FEApplication) {

    @Provides @Singleton internal fun provideContext(): Context = context

    @Provides @Singleton internal fun provideLoginManager(): LoginManager = LoginManager.getInstance()

    @Provides @Singleton internal fun provideTwitterAuthClient(): TwitterAuthClient = TwitterAuthClient()
}
