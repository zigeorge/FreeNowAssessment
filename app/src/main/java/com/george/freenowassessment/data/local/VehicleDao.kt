package com.george.freenowassessment.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * [VehicleDB] access object
 * */
@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vehicle: List<Vehicle>)

    @Query("SELECT * FROM vehicles WHERE bound = :bound AND state = :state")
    fun vehiclesInPageSource(bound: String, state: String): PagingSource<Int, Vehicle>

    @Query("SELECT * FROM vehicles WHERE bound = :bound AND state = :state")
    fun vehiclesInFlow(bound: String, state: String): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles WHERE bound = :bound")
    fun allVehicles(bound: String): List<Vehicle>

    @Query("DELETE FROM vehicles WHERE bound = :bound")
    suspend fun deleteAll(bound: String)

    @Query("SELECT COUNT(id) FROM vehicles WHERE bound = :bound AND state = :state")
    suspend fun countVehicles(bound: String, state: String): Int

}