package com.george.freenowassessment.repositories

import android.location.Geocoder
import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.ui.vo.Address
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
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
        dao.deleteAll()
        val list = api.getVehicleList(
            coordinate1.latitude,
            coordinate1.longitude,
            coordinate2.latitude,
            coordinate2.longitude
        ).body()?.poiList ?: ArrayList()
        if (list.isNotEmpty()) {
            val bound = coordinate1.toString() + coordinate2.toString()
            storeVehicles(list, bound)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun storeVehicles(list: List<VehicleData>, bound: String) {
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

fun Coordinate.address(geocoder: Geocoder): Address {
    val addresses = geocoder.getFromLocation(
        latitude,
        longitude, 1
    )
    return Address(addresses[0].featureName, addresses[0].getAddressLine(0))
}