package com.foodenak.itpscanner.ui.redeem

import com.foodenak.itpscanner.model.EventModel
import com.foodenak.itpscanner.model.UserModel
import com.foodenak.itpscanner.utils.ActivityScope
import dagger.Module
import dagger.Provides

/**
 * Created by ITP on 10/10/2015.
 */
@Module
class Module(private val eventId: Long, private val userId: String, private val deviceId: String?) {

    @Provides @ActivityScope
    internal fun provideViewModel(eventModel: EventModel, userModel: UserModel): RedeemViewModel {
        return RedeemViewModel(userId, eventId, deviceId, eventModel, userModel)
    }
}