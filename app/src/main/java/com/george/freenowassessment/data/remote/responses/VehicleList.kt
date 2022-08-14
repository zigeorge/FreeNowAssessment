package com.george.freenowassessment.data.remote.responses

data class VehicleList(
    val poiList: List<VehicleData>
)

data class VehicleData(
    val id: Long,
    val coordinate: Coordinate,
    val state: String,
    val fleetType: String,
    val heading: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        return other.hashCode() == hashCode()
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + coordinate.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + fleetType.hashCode()
        result = 31 * result + heading.hashCode()
        return result
    }
}

data class Coordinate(
    val latitude: Double,
    val longitude: Double
) {
    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun toString(): String {
        return "${latitude}_${longitude}"
    }

    companion object {
        fun fromString(string: String): Coordinate? {
            val split = string.split("_")
            return try {
                Coordinate(split[0].toDouble(), split[1].toDouble())
            } catch (ex: NumberFormatException) {
                null
            }
        }
    }

}
