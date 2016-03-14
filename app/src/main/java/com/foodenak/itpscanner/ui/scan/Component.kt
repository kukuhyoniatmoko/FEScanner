package com.foodenak.itpscanner.ui.scan

import com.foodenak.itpscanner.FEApplicationComponent
import com.foodenak.itpscanner.utils.ActivityScope
import dagger.Component

/**
 * Created by ITP on 10/6/2015.
 */
@ActivityScope
@Component(dependencies = arrayOf(FEApplicationComponent::class), modules = arrayOf(Module::class))
interface Component {

    fun inject(activity: ScanActivity)

    fun inject(fragment: ScanEnablerFragment)

    fun inject(fragment: ScanFragment)

    fun inject(fragment: HistoryFragment)

    fun inject(fragment: ScanResultFragment)

    fun inject(fragment: InvalidQRCodeFragment)
}