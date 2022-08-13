package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate

interface VehicleListRepository {
    suspend fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    )

    fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle>
}