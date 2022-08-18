package com.george.freenowassessment.data.local

import android.location.Geocoder
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.george.freenowassessment.data.local.Vehicle.Companion.ACTIVE
import com.george.freenowassessment.data.local.Vehicle.Companion.INACTIVE
import com.george.freenowassessment.data.remote.responses.VehicleData
import com.george.freenowassessment.other.Constants.VEHICLE_TABLE
import com.george.freenowassessment.other.address
import com.george.freenowassessment.other.toJson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/** [Vehicle] entity for Room DB */
@Entity(tableName = VEHICLE_TABLE)
class Vehicle(
    var vehicleId: Long,
    var latitude: Double,
    var longitude: Double,
    var address: String = "",
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

    fun equals(vehicleData: VehicleData): Boolean {
        return this.vehicleId == vehicleData.id
                && this.latitude == vehicleData.coordinate.latitude
                && this.longitude == vehicleData.coordinate.longitude
                && this.state == vehicleData.state
                && this.heading == vehicleData.heading
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

    companion object {
        // these constants represents vehicles state
        const val ACTIVE = "ACTIVE"
        const val INACTIVE = "INACTIVE"
    }
}

@OptIn(DelicateCoroutinesApi::class)
class VehicleList(val vehicles: ArrayList<Vehicle>) {

    fun hasVehicle(id: Long): Boolean {
        vehicles.forEach {
            if (it.vehicleId == id)
                return true
        }
        return false
    }

    fun getVehicle(id: Long): Vehicle? {
        vehicles.forEach {
            if (it.vehicleId == id)
                return it
        }
        return null
    }

    fun update(data: VehicleData, geocoder: Geocoder) {
        // GlobalScope is used to run the insert operation in IO dispatcher as
        // getting address from geocoder blocks the main thread otherwise
        GlobalScope.launch(Dispatchers.IO) {
            val vehicle = getVehicle(data.id)
            vehicle?.let {
                it.latitude = data.coordinate.latitude
                it.longitude = data.coordinate.longitude
                it.address = data.coordinate.address(geocoder).toJson()
                it.state = data.state
                it.heading = data.heading
            }
        }
    }

    fun deactivateAll() {
        vehicles.forEach {
            it.state = INACTIVE
        }
    }

    fun add(data: VehicleData, geocoder: Geocoder, bound: String) {
        // GlobalScope is used to run the insert operation in IO dispatcher as
        // getting address from geocoder blocks the main thread otherwise
        GlobalScope.launch(Dispatchers.IO) {
            vehicles.add(
                Vehicle(
                    data.id,
                    data.coordinate.latitude,
                    data.coordinate.longitude,
                    data.coordinate.address(geocoder).toJson(),
                    data.fleetType,
                    data.state,
                    bound,
                    data.heading
                )
            )
        }
    }
}