package com.foodenak.itpscanner.ui.scan

import dagger.Module
import dagger.Provides

/**
 * Created by ITP on 10/11/2015.
 */
@Module
class Module(val activity: ScanActivity) {

    @Provides internal fun provideCallback(): ScanFragment.Callback = activity
}