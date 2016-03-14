package com.foodenak.itpscanner.ui.login

import com.foodenak.itpscanner.FEApplicationComponent
import com.foodenak.itpscanner.utils.ActivityScope
import dagger.Component

/**
 * Created by ITP on 10/5/2015.
 */
@ActivityScope
@Component(dependencies = arrayOf(FEApplicationComponent::class), modules = arrayOf(Module::class))
interface Component {

    fun inject(activity: LoginActivity)

    fun inject(fragment: LoginFragment)
}
