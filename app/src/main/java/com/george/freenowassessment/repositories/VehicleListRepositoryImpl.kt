package com.george.freenowassessment.repositories

import android.location.Geocoder
import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.data.remote.responses.Coordinate
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
        val list = api.getVehicleList(
            coordinate1.latitude,
            coordinate1.longitude,
            coordinate2.latitude,
            coordinate2.longitude
        ).body()?.poiList ?: ArrayList()
        if (list.isNotEmpty()) {
            val vehicles = ArrayList<Vehicle>()
            val bound = coordinate1.toString() + coordinate2.toString()
            for (vehicle in list) {
                vehicles.add(
                    Vehicle(
                        vehicle.id,
                        vehicle.coordinate.latitude,
                        vehicle.coordinate.longitude,
                        vehicle.coordinate.address(geocoder),
                        vehicle.fleetType,
                        vehicle.state,
                        bound,
                        vehicle.heading
                    )
                )
            }
            dao.insertAll(vehicles)
        }
    }

    override fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle> {
        return dao.vehiclesInBound(coordinate1.toString() + coordinate2.toString())
    }
}

fun Coordinate.address(geocoder: Geocoder): String {
    val addresses = geocoder.getFromLocation(
        latitude,
        longitude, 1
    )
    return "${addresses[0].getAddressLine(0)}, " +
            "${addresses[0].locality}, ${addresses[0].adminArea}, " +
            "${addresses[0].countryName}, ${addresses[0].postalCode}, " +
            addresses[0].featureName
}