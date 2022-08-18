package com.george.freenowassessment.repositories

import android.location.Geocoder
import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.Vehicle.Companion.ACTIVE
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.other.address
import com.george.freenowassessment.other.exceptions.UnableToLoadException
import com.george.freenowassessment.other.exceptions.UnableToUpdateException
import com.george.freenowassessment.other.toJson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


class VehicleListRepositoryImpl @Inject constructor(
    private val api: VehicleApi,
    private val dao: VehicleDao,
    private val geocoder: Geocoder
) : VehicleListRepository {

    override suspend fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        try {
            val list = api.getVehicleList(
                coordinate1.latitude,
                coordinate1.longitude,
                coordinate2.latitude,
                coordinate2.longitude
            ).body()?.poiList ?: ArrayList()
            VehicleDataManager(list, dao, geocoder)
                .storeData(coordinate1.toString() + coordinate2.toString())
        } catch (ex: Exception) {
            val count = dao.countVehicles(
                coordinate1.toString() + coordinate2.toString(),
                ACTIVE
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