package com.george.freenowassessment.di

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.george.freenowassessment.data.local.VehicleDB
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.other.Constants.BASE_URL
import com.george.freenowassessment.other.Constants.DB_NAME
import com.george.freenowassessment.repositories.VehicleListRepository
import com.george.freenowassessment.repositories.VehicleListRepositoryImpl
import com.george.freenowassessment.ui.VehicleRecyclerViewAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideApiClient(): VehicleApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(VehicleApi::class.java)

    @Singleton
    @Provides
    fun provideGeoCoder(
        @ApplicationContext context: Context
    ) = Geocoder(context)

    @Singleton
    @Provides
    fun provideVehicleRecyclerViewAdapter() = VehicleRecyclerViewAdapter()

    @Singleton
    @Provides
    fun provideDB(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, VehicleDB::class.java, DB_NAME).build()

    @Singleton
    @Provides
    fun provideVehicleDao(
        db: VehicleDB
    ) = db.vehicleDao()

    @Singleton
    @Provides
    fun provideRepository(
        dao: VehicleDao,
        api: VehicleApi,
        geocoder: Geocoder
    ) = VehicleListRepositoryImpl(api, dao, geocoder) as VehicleListRepository

}