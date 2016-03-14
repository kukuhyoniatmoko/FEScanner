package com.foodenak.itpscanner

import android.app.Application
import com.facebook.FacebookSdk
import com.foodenak.itpscanner.services.UserSession
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterCore
import io.fabric.sdk.android.Fabric
import rx.schedulers.Schedulers

/**
 * Created by ITP on 10/5/2015.
 */
class FEApplication : Application() {

  private lateinit var component: FEApplicationComponent

    val dependencyHolder = DependencyHolder()

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this);
        var twitterConfig = TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(Fabric.Builder(this)
                .kits(TwitterCore(twitterConfig))
                .debuggable(BuildConfig.DEBUG)
                .build());
        UserSession.initialize(this)
        component = createComponent(FEApplicationModule(this));
        if (UserSession.currentSession.isActive()) component().userModel().getUser(UserSession.currentSession.id)
                .subscribeOn(Schedulers.io())
                .subscribe({ user -> UserSession.currentSession.user = user }, {})
    }

  fun component() = component

  fun createComponent(module: FEApplicationModule): FEApplicationComponent {
    return DaggerFEApplicationComponent.builder().fEApplicationModule(module).build()
    }
}