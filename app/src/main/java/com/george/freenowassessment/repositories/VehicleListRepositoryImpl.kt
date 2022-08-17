package com.george.freenowassessment.repositories

import android.location.Geocoder
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
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
        // TODO: refactor to work on existing data
        // TODO: handle socket timeout
        // TODO: handle format exception
        // TODO: handle other exception
        try {
            /** deleting all [Vehicle] in local DB before getting new [VehicleData] from API*/
            dao.deleteAll()
            val list = api.getVehicleList(
                coordinate1.latitude,
                coordinate1.longitude,
                coordinate2.latitude,
                coordinate2.longitude
            ).body()?.poiList ?: ArrayList()
            if (list.isNotEmpty()) {
                // TODO: Remove log
                Log.e("SIZE", list.size.toString())
                /** @bound is used to store each vehicle data so that the system
                 * can be scaled for a different bound */
                val bound = coordinate1.toString() + coordinate2.toString()
                storeVehicles(list, bound)
            }
        } catch (ex: Exception) {
            val count = dao.countVehicles()
            if (count > 0) {
                throw UnableToUpdateException("Unable to update new data")
            }
            throw UnableToLoadException("Unable to load new data")
        }
    }

    /** A separate coroutine to insert [VehicleData] in local db*/
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun storeVehicles(list: List<VehicleData>, bound: String) {
        // GlobalScope is used to run the insert operation in different dispatcher as
        // getting address from geocoder blocks the main thread otherwise
        GlobalScope.launch(Dispatchers.IO) {
            for (vehicle in list) {
                dao.insert(
                    Vehicle(
                        vehicle.id,
                        vehicle.coordinate.latitude,
                        vehicle.coordinate.longitude,
                        vehicle.coordinate.address(geocoder).toJson(),
                        vehicle.fleetType,
                        vehicle.state,
                        bound,
                        vehicle.heading
                    )
                )
            }
        }
    }

    override fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle> {
        return dao.vehiclesInBound(coordinate1.toString() + coordinate2.toString())
    }

    override fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>> {
        return dao.allVehiclesInBound(coordinate1.toString() + coordinate2.toString())
    }
}