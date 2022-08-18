package com.george.freenowassessment.repositories

import android.location.Geocoder
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.local.VehicleList
import com.george.freenowassessment.data.remote.responses.VehicleData
import javax.inject.Inject

/**
 * @param list represents list of [VehicleData] obtained from API
 * @param dao represents database access object
 * @param geocoder represents Android [Geocoder] API instance
 * */
class VehicleDataManager(
    private val list: List<VehicleData>,
    private val dao: VehicleDao,
    private val geocoder: Geocoder
) {
    /**
    * this function update all local data from db with [VehicleData] from API
    * */
    suspend fun storeData(bound: String) {
        val vehicles = VehicleList(dao.allVehicles(bound) as ArrayList<Vehicle>)
        vehicles.deactivateAll()

        list.forEach { data ->
            if (vehicles.hasVehicle(data.id)) {
                val vehicle = vehicles.getVehicle(data.id)
                if(vehicle?.equals(data) == false) {
                    vehicles.update(data, geocoder)
                } else {
                    vehicle?.state = data.state
                }
            } else {
                vehicles.add(data, geocoder, bound)
            }
        }
        dao.deleteAll(bound)
        dao.insertAll(vehicles.vehicles)
    }

}