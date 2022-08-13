package com.george.freenowassessment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** [VehicleDB] to help cache the [Vehicle] data for pagination */

@Database(
    entities = [Vehicle::class],
    version = 1,
    exportSchema = false
)
abstract class VehicleDB : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
}