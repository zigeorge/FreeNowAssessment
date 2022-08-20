package com.george.freenowassessment.data.local

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * [VehicleDB] access object
 * */
@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles WHERE bound = :bound AND state = :state")
    fun vehiclesInPageSource(bound: String, state: String): PagingSource<Int, Vehicle>

    @Query("SELECT * FROM vehicles WHERE bound = :bound AND state = :state")
    fun vehiclesInFlow(bound: String, state: String): Flow<List<Vehicle>>

    @Query("SELECT COUNT(id) FROM vehicles WHERE bound = :bound")
    fun count(bound: String): Int

    @Update
    suspend fun updateVehicle(vehicle: Vehicle)

    @Query("SELECT * FROM vehicles WHERE vehicleId = :vehicleId")
    suspend fun getVehicle(vehicleId: Long): Vehicle?

    @Query("DELETE FROM vehicles WHERE vehicleId NOT IN (:list)")
    suspend fun deleteVehicles(list: Array<Long>)

    @Query("SELECT vehicleId FROM vehicles WHERE bound = :bound")
    fun getAllIds(bound: String): List<Long>

}