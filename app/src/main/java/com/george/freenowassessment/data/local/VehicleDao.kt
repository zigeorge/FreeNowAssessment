package com.george.freenowassessment.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * [VehicleDB] access object
 * */
@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles WHERE bound = :bound")
    fun vehiclesInBound(bound: String): PagingSource<Int, Vehicle>

    @Query("SELECT * FROM vehicles WHERE bound = :bound")
        fun allVehiclesInBound(bound: String): Flow<List<Vehicle>>

    @Query("DELETE FROM vehicles")
    suspend fun deleteAll()

}