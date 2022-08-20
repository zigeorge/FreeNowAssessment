package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.responses.Coordinate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class VehicleListRepositoryImpl @Inject constructor(
    private val dao: VehicleDao,
    private val vehicleDataSource: VehicleDataSource
) : VehicleListRepository {

    override fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        vehicleDataSource
            .storeData(coordinate1, coordinate2)
    }

    override fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle> {
        return dao.vehiclesInPageSource(
            coordinate1.toString() + coordinate2.toString()
        )
    }

    override fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>> {
        return dao.vehiclesInFlow(
            coordinate1.toString() + coordinate2.toString()
        )
    }

    override fun getAllIds(coordinate1: Coordinate, coordinate2: Coordinate): List<Long> {
        return dao.getAllIds(coordinate1.toString()+coordinate2.toString())
    }
}