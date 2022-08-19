package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.Vehicle.Companion.ACTIVE
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.other.exceptions.UnableToLoadException
import com.george.freenowassessment.other.exceptions.UnableToUpdateException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class VehicleListRepositoryImpl @Inject constructor(
    private val dao: VehicleDao,
    private val vehicleDataSource: VehicleDataSource
) : VehicleListRepository {

    override suspend fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        try {
            vehicleDataSource
                .storeData(coordinate1, coordinate2)
        } catch (ex: Exception) {
            val count = dao.count(
                coordinate1.toString() + coordinate2.toString()
            )
            if (count > 0) {
                throw UnableToUpdateException("Unable to update new data")
            }
            throw UnableToLoadException("Unable to load new data")
        }
    }

    override fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle> {
        return dao.vehiclesInPageSource(
            coordinate1.toString() + coordinate2.toString(),
            ACTIVE
        )
    }

    override fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>> {
        return dao.vehiclesInFlow(
            coordinate1.toString() + coordinate2.toString(),
            ACTIVE
        )
    }
}