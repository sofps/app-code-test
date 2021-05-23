package com.codetest.main.data.model

enum class Status {
    CLOUDY,
    SUNNY,
    MOSTLY_SUNNY,
    PARTLY_SUNNY,
    PARTLY_SUNNY_RAIN,
    THUNDER_CLOUD_AND_RAIN,
    TORNADO,
    BARELY_SUNNY,
    LIGHTENING,
    SNOW_CLOUD,
    RAINY;
}

data class GetLocationsResponse(
    val locations: List<LocationApiModel>
)

data class LocationApiModel(
    val id: String,
    val name: String?,
    val temperature: String?,
    val status: Status
)
