package com.george.freenowassessment.repositories

import androidx.lifecycle.LiveData
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.Vehicle

interface VehicleListRepository {
    suspend fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): List<Vehicle>?
}