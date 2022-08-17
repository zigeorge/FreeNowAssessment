package com.george.freenowassessment.other

import com.george.freenowassessment.data.remote.responses.Coordinate

object Constants {
    const val BASE_URL = "https://fake-poi-api.mytaxi.com/"

    const val VEHICLE_TABLE = "vehicles"

    const val DB_NAME = "free_now_db"

    const val PAGE_SIZE = 20
    const val MAX_SIZE = 60

    val coordinate1 = Coordinate(53.694865,9.757589)
    val coordinate2 = Coordinate(53.394655,10.099891)

    val BOUND = coordinate1.toString() + coordinate2.toString()

}