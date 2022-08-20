package com.george.freenowassessment.di

import android.content.Context
import androidx.room.Room
import com.george.freenowassessment.data.local.VehicleDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestModule {

    @Provides
    @Named("test_vehicle_db")
    fun provideInMemoryDatabase(
        @ApplicationContext context: Context
    ) = Room.inMemoryDatabaseBuilder(context, VehicleDB::class.java)
        .allowMainThreadQueries()
        .build()
}