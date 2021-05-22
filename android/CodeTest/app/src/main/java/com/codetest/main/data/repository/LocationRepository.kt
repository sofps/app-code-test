package com.codetest.main.data.repository

import com.codetest.main.data.api.LocationApiService
import com.codetest.main.model.Location
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationApiService: LocationApiService
) {

    fun getLocations(callback: (List<Location>?) -> Unit) {
        val locations: ArrayList<Location> = arrayListOf()
        locationApiService.getLocations({
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