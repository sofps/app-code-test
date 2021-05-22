package com.codetest.main.data.model

enum class Status(val value: Int) {
    CLOUDY(0x2601),
    SUNNY(0x2600),
    MOSTLY_SUNNY(0x1F324),
    PARTLY_SUNNY(0x26C5),
    PARTLY_SUNNY_RAIN(0x1F326),
    THUNDER_CLOUD_AND_RAIN(0x26C8),
    TORNADO(0x1F32A),
    BARELY_SUNNY(0x1F325),
    LIGHTENING(0x1F329),
    SNOW_CLOUD(0x1F328),
    RAINY(0x1F327);
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
