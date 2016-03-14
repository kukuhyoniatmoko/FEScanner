package com.foodenak.itpscanner.ui.redeem

import com.foodenak.itpscanner.FEApplicationComponent
import com.foodenak.itpscanner.utils.ActivityScope
import dagger.Component

/**
 * Created by ITP on 10/9/2015.
 */
@ActivityScope
@Component(dependencies = arrayOf(FEApplicationComponent::class), modules = arrayOf(Module::class))
interface Component {

    fun inject(fragment: RedeemFragment)

    fun inject(fragment: GoBackConfirmationFragment)

    fun inject(fragment: UnSelectVoucherConfirmationFragment)

    fun inject(activity: RedeemActivity)
}