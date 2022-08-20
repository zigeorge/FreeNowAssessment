package com.george.freenowassessment.repositories

import android.location.Geocoder
import android.util.Log
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.remote.VehicleApi
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.other.getVehicle
import kotlinx.coroutines.*
import javax.inject.Inject

class VehicleDataSource @Inject constructor(
    private val api: VehicleApi,
    private val dao: VehicleDao,
    private val geocoder: Geocoder
) {

    /**
     * this function update all local data from db with [VehicleData] from API
     * it also simultaneously request API to update the local db
     * */
    fun storeData(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        /**
         * [CoroutineExceptionHandler] used to ignore any exception that may occurs
         * during this process
        * */
        val handler = CoroutineExceptionHandler { _, ex ->
            print(ex.message)
        }
        CoroutineScope(Dispatchers.IO+handler).launch {
            // store and update db with list from api
            while (true) {
                val bound = coordinate1.toString() + coordinate2.toString()
                // get vehicle list from API
                val list = api.getVehicleList(
                    coordinate1.latitude,
                    coordinate1.longitude,
                    coordinate2.latitude,
                    coordinate2.longitude
                ).body()?.poiList ?: ArrayList()
                Log.e("SIZE-API", list.size.toString())
                val ids = ArrayList<Long>()
                list.forEach {
                    ids.add(it.id)
                }
                dao.deleteVehicles(ids.toTypedArray())
                val count = dao.count(bound)
                list.forEach { data ->
                    if (count == 0) {
                        dao.insert(data.getVehicle(geocoder, bound))
                    } else {
                        Log.e("UPDATE", "UPDATING DATA")
                        updateVehicles(data, bound)
                    }
                }
                delay(2000)
            }
        }
    }

    /**
     * update if found in DB
     * insert [VehicleData] otherwise
     * */
    private suspend fun updateVehicles(data: VehicleData, bound: String) {
        val aVehicle = dao.getVehicle(data.id)
        aVehicle?.let {
            if (!it.equals(data)) {
                dao.updateVehicle(data.getVehicle(geocoder, bound))
            }
        } ?: run {
            dao.insert(data.getVehicle(geocoder, bound))
        }
    }
}