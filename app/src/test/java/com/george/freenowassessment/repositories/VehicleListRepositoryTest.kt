package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.internal.immutableListOf

class VehicleListRepositoryTest: VehicleListRepository {

    private var vehicles: List<Vehicle> = immutableListOf()
    private val vehicleFactory = VehicleFactory()

    private var vehiclesFlow: Flow<List<Vehicle>> = emptyFlow()

    override fun loadVehicleList(coordinate1: Coordinate, coordinate2: Coordinate) {
        vehicles = vehicleFactory.fakeVehicles()
        vehiclesFlow = flowOf(vehicles)
    }

    override fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle> {
        TODO("Not yet implemented")
    }

    override fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>> {
        return vehiclesFlow
    }

    override fun getAllIds(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): List<Long> {
        val list = ArrayList<Long>()
        vehicles.forEach {
            list.add(it.vehicleId)
        }
        return list
    }
}