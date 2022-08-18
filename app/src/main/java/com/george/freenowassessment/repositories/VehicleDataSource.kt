package com.george.freenowassessment.repositories

import android.location.Geocoder
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.Vehicle.Companion.ACTIVE
import com.george.freenowassessment.data.local.Vehicle.Companion.INACTIVE
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.local.VehicleList
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.other.getVehicle
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class VehicleDataSource @Inject constructor(
    private val api: VehicleApi,
    private val dao: VehicleDao,
    private val geocoder: Geocoder
) {

    /**
     * this function update all local data from db with [VehicleData] from API
     * */
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun storeData(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        val bound = coordinate1.toString() + coordinate2.toString()
        // get vehicle list from API
        val list = api.getVehicleList(
            coordinate1.latitude,
            coordinate1.longitude,
            coordinate2.latitude,
            coordinate2.longitude
        ).body()?.poiList ?: ArrayList()
        // store and update db with list from api
        GlobalScope.launch(Dispatchers.IO) {
            dao.deactivateAll(INACTIVE, bound)
            list.forEach { data ->
                val aVehicle = dao.getVehicle(data.id)
                aVehicle?.let {
                    if (!it.equals(data)) {
                        dao.updateVehicle(data.getVehicle(geocoder, bound))
                    } else {
                        it.state = ACTIVE
                        dao.updateVehicle(it)
                    }
                } ?: run {
                    dao.insert(data.getVehicle(geocoder, bound))
                }
            }
        }
    }
}