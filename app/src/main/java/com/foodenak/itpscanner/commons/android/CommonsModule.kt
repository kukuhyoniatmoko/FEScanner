package com.foodenak.itpscanner.commons.android

import android.content.Context
import com.foodenak.itpscanner.commons.NetworkChecker
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by ITP on 5/23/16.
 */
@Module class CommonsModule {
  @Provides @Singleton fun provideNetworkChecker(context: Context): NetworkChecker {
    return AndroidNetworkChecker(context)
  }
}