package com.george.freenowassessment.data.remote

import com.george.freenowassessment.data.remote.responses.VehicleList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface VehicleApi {

    @GET(".")
    suspend fun getVehicleList(@Query("p1Lat") lat1: Double,
                       @Query("p1Lon") lon1: Double,
                       @Query("p2Lat") lat2: Double,
                       @Query("p2Lon") lon2: Double
    ): Response<VehicleList>
}