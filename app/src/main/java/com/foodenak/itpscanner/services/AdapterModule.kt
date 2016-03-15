package com.foodenak.itpscanner.services

import android.content.Context
import com.foodenak.foodenak.rest.ServiceEndPoints
import com.foodenak.itpscanner.services.deserializer.BooleanDeserializer
import com.foodenak.itpscanner.services.deserializer.DateDeserializer
import com.foodenak.itpscanner.services.serializer.DateSerializer
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.BaseUrl
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Converter.Factory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by ITP on 10/5/2015.
 */
@Module
class AdapterModule {

  @Provides @Singleton internal fun provideBaseUrl(context: Context): BaseUrl = ServiceEndPoints(
      context)

  @Provides @Singleton internal fun provideRestAdapter(baseUrl: BaseUrl,
      @Named(ClientModule.DEFAULT_CLIENT_PROVIDER) client: OkHttpClient,
      converter: Converter.Factory, callAdapter: CallAdapter.Factory): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addCallAdapterFactory(callAdapter)
        .addConverterFactory(converter)
        .build()
  }

  @Provides @Singleton @Named(POOL_ADAPTER) internal fun providePoolRestAdapter(baseUrl: BaseUrl,
      @Named(ClientModule.POOL_CLIENT_PROVIDER) client: OkHttpClient,
      converter: Factory, callAdapter: CallAdapter.Factory): Retrofit {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(converter)
        .addCallAdapterFactory(callAdapter)
        .build()
  }

  @Provides @Singleton fun provideCallAdapterFactory(): CallAdapter.Factory = RxJavaCallAdapterFactory.create();

  @Provides @Singleton internal fun provideConverter(): Converter.Factory {
    val gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, BooleanDeserializer())
        .registerTypeAdapter(Date::class.java, DateSerializer())
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
    return GsonConverterFactory.create(gson);
  }

  companion object {

    const val POOL_ADAPTER = "POOL_ADAPTER";
  }
}
