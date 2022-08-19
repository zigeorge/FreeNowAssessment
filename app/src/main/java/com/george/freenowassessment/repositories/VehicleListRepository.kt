package com.george.freenowassessment.repositories

import androidx.paging.PagingSource
import com.george.freenowassessment.data.local.Vehicle
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.data.remote.responses.VehicleData
import kotlinx.coroutines.flow.Flow

interface VehicleListRepository {

    /** loading all [VehicleData] from API
     *  @param coordinate1 in the bounds of Hamburg
     * @param coordinate2 in the bounds of Hamburg
     * */
    fun loadVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    )

    /** get vehicle list from local db to display as list
     * @param coordinate1 in the bounds of Hamburg
     * @param coordinate2 in the bounds of Hamburg
     * */
    fun getVehicleList(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): PagingSource<Int, Vehicle>

    /** get vehicle list from local db to display as markers in map
     * @param coordinate1 in the bounds of Hamburg
     * @param coordinate2 in the bounds of Hamburg
     * */
    fun getAllVehicle(
        coordinate1: Coordinate,
        coordinate2: Coordinate
    ): Flow<List<Vehicle>>
}