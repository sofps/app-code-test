package com.codetest.main.ui

import com.codetest.R
import com.codetest.main.data.model.Status
import com.codetest.main.domain.Location

data class LocationUI(
    val id: String,
    val name: String?,
    val weather: String,
    val status: StatusUI
)

enum class StatusUI(val value: Int, val color: Int) {
    CLOUDY(0x2601, R.color.grey),
    SUNNY(0x2600, R.color.blue),
    MOSTLY_SUNNY(0x1F324, R.color.blue),
    PARTLY_SUNNY(0x26C5, R.color.blue),
    PARTLY_SUNNY_RAIN(0x1F326, R.color.blue),
    THUNDER_CLOUD_AND_RAIN(0x26C8, R.color.red),
    TORNADO(0x1F32A, R.color.red),
    BARELY_SUNNY(0x1F325, R.color.blue),
    LIGHTENING(0x1F329, R.color.red),
    SNOW_CLOUD(0x1F328, R.color.grey),
    RAINY(0x1F327, R.color.grey);
}

fun Location.toLocationUI(): LocationUI {
    val statusUI = this.status.toStatusUI()
    return LocationUI(
        id = this.id,
        name = this.name,
        weather = "${this.temperature}°C ${String(Character.toChars(statusUI.value))}",
        status = statusUI
    )
}

fun Status.toStatusUI() = when(this) {
    Status.CLOUDY -> StatusUI.CLOUDY
    Status.SUNNY -> StatusUI.SUNNY
    Status.MOSTLY_SUNNY -> StatusUI.MOSTLY_SUNNY
    Status.PARTLY_SUNNY -> StatusUI.PARTLY_SUNNY
    Status.PARTLY_SUNNY_RAIN -> StatusUI.PARTLY_SUNNY_RAIN
    Status.THUNDER_CLOUD_AND_RAIN -> StatusUI.THUNDER_CLOUD_AND_RAIN
    Status.TORNADO -> StatusUI.TORNADO
    Status.BARELY_SUNNY -> StatusUI.BARELY_SUNNY
    Status.LIGHTENING -> StatusUI.LIGHTENING
    Status.SNOW_CLOUD -> StatusUI.SNOW_CLOUD
    Status.RAINY -> StatusUI.RAINY
}
