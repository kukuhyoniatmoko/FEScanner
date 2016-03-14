package com.foodenak.itpscanner.services

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Module
class ServiceModule {

    @Provides
    @Singleton
    internal fun provideUserService(adapter: Retrofit): UserService {
        return adapter.create(UserService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideEventService(adapter: Retrofit): EventService {
        return adapter.create(EventService::class.java)
    }

    @Provides
    @Singleton
    internal fun providePoolService(@Named(AdapterModule.POOL_ADAPTER) adapter: Retrofit): PoolService {
        return adapter.create(PoolService::class.java)
    }
}