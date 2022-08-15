package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface VehicleListRepository {
    suspend fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    )

    fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle>

    fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>>
}