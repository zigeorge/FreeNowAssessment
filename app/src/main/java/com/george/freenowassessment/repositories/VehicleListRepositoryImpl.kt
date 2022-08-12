package com.george.freenowassessment.repositories

import com.george.freenowassessment.data.remote.FreeNowApi
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.Vehicle
import javax.inject.Inject

class VehicleListRepositoryImpl @Inject constructor(
    private val api: FreeNowApi
) : VehicleListRepository {
    /* @param coordinate1 and @param coordinate2 represents the bound within which available vehicle
    * list will be retrieved from api*/
    override suspend fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): List<Vehicle>? {
        val response = api.getVehicleList(
            coordinate1.latitude,
            coordinate1.longitude,
            coordinate2.latitude,
            coordinate2.longitude
        )
        if(response.isSuccessful && response.code() == 200 && response.body() != null) {
            return response.body()?.poiList
        }
        return null
    }

}