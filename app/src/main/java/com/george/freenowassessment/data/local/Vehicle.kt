package com.george.freenowassessment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.george.freenowassessment.data.remote.responses.Coordinate
import com.george.freenowassessment.other.Constants.VEHICLE_TABLE

/** [Vehicle] entity for Room DB */
@Entity(tableName = VEHICLE_TABLE)
class Vehicle(
    var vehicleId: Long,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var type: String,
    var state: String,
    var bound: String,
    var heading: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other?.javaClass != javaClass) return false
        return other.hashCode() == hashCode()
    }

    override fun hashCode(): Int {
        var result = vehicleId.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + bound.hashCode()
        result = 31 * result + heading.hashCode()
        result = 31 * result + (id ?: 0)
        return result
    }
}