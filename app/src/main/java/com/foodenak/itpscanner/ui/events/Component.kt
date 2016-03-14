package com.foodenak.itpscanner.ui.events

import com.foodenak.itpscanner.FEApplicationComponent
import com.foodenak.itpscanner.utils.ActivityScope
import dagger.Component

/**
 * Created by ITP on 10/7/2015.
 */
@ActivityScope
@Component(dependencies = arrayOf(FEApplicationComponent::class))
interface Component {

    fun inject(activity: EventsActivity)

    fun inject(fragment: EventsFragment)

    fun inject(fragment: SelectEventConfirmationDialog)
}
