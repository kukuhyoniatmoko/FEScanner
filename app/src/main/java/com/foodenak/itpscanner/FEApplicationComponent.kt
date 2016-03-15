package com.foodenak.itpscanner

import com.facebook.login.LoginManager
import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.persistence.DbModule
import com.foodenak.itpscanner.persistence.dao.DaoSession
import com.foodenak.itpscanner.services.AdapterModule
import com.foodenak.itpscanner.services.ClientModule
import com.foodenak.itpscanner.services.ServiceModule
import com.foodenak.itpscanner.services.image.ImageLoader
import com.foodenak.itpscanner.services.image.ImageLoaderModule
import com.foodenak.itpscanner.ui.events.EventsViewModel
import com.foodenak.itpscanner.ui.login.LoginViewModel
import com.foodenak.itpscanner.ui.scan.HistoryViewModel
import com.foodenak.itpscanner.ui.scan.ScanViewModel
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import dagger.Component
import javax.inject.Singleton

/**
 * Created by ITP on 10/5/2015.
 */
@Singleton
@Component(modules = arrayOf(FEApplicationModule::class,
    AdapterModule::class,
    ClientModule::class,
    ServiceModule::class,
    DbModule::class,
    ImageLoaderModule::class)) interface FEApplicationComponent {

  fun scanViewModel(): ScanViewModel

  fun loginViewModel(): LoginViewModel

  fun eventsViewModel(): EventsViewModel

  fun historyViewModel(): HistoryViewModel

  fun eventModel(): EventModel

  fun userModel(): UserModel

  fun imageLoader(): ImageLoader

  fun loginManager(): LoginManager

  fun twitterAuthClient(): TwitterAuthClient

  fun daoSession(): DaoSession
}