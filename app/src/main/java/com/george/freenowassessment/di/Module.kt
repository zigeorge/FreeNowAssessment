package com.george.freenowassessment.di

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.george.freenowassessment.data.local.VehicleDB
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.other.Constants.BASE_URL
import com.george.freenowassessment.other.Constants.DB_NAME
import com.george.freenowassessment.other.connectivity.ConnectivityObserver
import com.george.freenowassessment.other.connectivity.NetworkConnectivityObserver
import com.george.freenowassessment.repositories.VehicleDataSource
import com.george.freenowassessment.repositories.VehicleListRepository
import com.george.freenowassessment.repositories.VehicleListRepositoryImpl
import com.george.freenowassessment.ui.adapters.VehicleRecyclerViewAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Singleton
    @Provides
    fun provideApiClient(): VehicleApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(VehicleApi::class.java)
    }
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
    fun provideVehicleDataManager(
        api: VehicleApi,
        dao: VehicleDao,
        geocoder: Geocoder
    ) = VehicleDataSource(api, dao, geocoder)

    @Singleton
    @Provides
    fun provideRepository(
        dao: VehicleDao,
        vehicleDataSource: VehicleDataSource
    ) = VehicleListRepositoryImpl(dao, vehicleDataSource) as VehicleListRepository

    @Singleton
    @Provides
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ) = NetworkConnectivityObserver(context) as ConnectivityObserver

}