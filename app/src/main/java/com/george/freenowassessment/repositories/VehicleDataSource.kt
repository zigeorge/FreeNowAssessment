package com.george.freenowassessment.repositories

import android.location.Geocoder
import android.util.Log
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.local.Vehicle.Companion.ACTIVE
import com.george.freenowassessment.data.local.Vehicle.Companion.INACTIVE
import com.george.freenowassessment.data.local.VehicleDao
import com.george.freenowassessment.data.local.VehicleList
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
     * */
    suspend fun storeData(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ) {
        try {
            // store and update db with list from api
            CoroutineScope(Dispatchers.IO).launch {
                while(true) {
                    val bound = coordinate1.toString() + coordinate2.toString()
                    // get vehicle list from API
                    val list = api.getVehicleList(
                        coordinate1.latitude,
                        coordinate1.longitude,
                        coordinate2.latitude,
                        coordinate2.longitude
                    ).body()?.poiList ?: ArrayList()
                    val ids = ArrayList<Long>()
                    list.forEach {
                        ids.add(it.id)
                    }
                    Log.e("COUNT-API", list.size.toString())
                    dao.deleteVehicles(ids.toTypedArray())
                    val count = dao.count(bound)
                    list.forEach { data ->
                        if (count == 0) {
                            dao.insert(data.getVehicle(geocoder, bound))
                        } else {
                            updateVehicles(data, bound)
                        }
                    }
                    Log.e("COUNT-DB", dao.count(bound).toString())
                    delay(5000)
                }
            }
        } catch (ex: Exception) {
            throw ex
        }
    }

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