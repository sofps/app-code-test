package com.codetest.main

import com.codetest.main.api.LocationApiService
import com.codetest.main.model.Location
import java.util.*
import javax.inject.Inject

class LocationHelper @Inject constructor(
    private val locationApiService: LocationApiService
) {

    fun getLocations(callback: (List<Location>?) -> Unit) {
        val locations: ArrayList<Location> = arrayListOf()
        locationApiService.get("locations", {
            val list = it.get("locations").asJsonArray
            for (json in list) {
                locations.add(Location.from(json.asJsonObject))
            }
            callback(locations)
        }, {
            callback(null)
        })
    }
}