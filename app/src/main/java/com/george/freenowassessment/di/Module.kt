package com.george.freenowassessment.di

import android.content.Context
import android.location.Geocoder
import com.george.freenowassessment.data.remote.FreeNowApi
import com.george.freenowassessment.other.Constants.BASE_URL
import com.george.freenowassessment.ui.VehicleRecyclerViewAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Module {

    @Singleton
    @Provides
    fun provideApiClient():
            FreeNowApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(FreeNowApi::class.java)

    @Singleton
    @Provides
    fun provideGeoCoder(
        @ApplicationContext context: Context
    ): Geocoder = Geocoder(context)

    @Singleton
    @Provides
    fun provideVehicleRecyclerViewAdapter():
            VehicleRecyclerViewAdapter = VehicleRecyclerViewAdapter()

}