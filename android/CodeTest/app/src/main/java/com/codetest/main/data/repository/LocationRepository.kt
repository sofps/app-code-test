package com.codetest.main.data.repository

import com.codetest.main.data.api.LocationApiService
import com.codetest.main.data.model.LocationApiModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationApiService: LocationApiService
) {

    fun getLocations(callback: (List<LocationApiModel>?) -> Unit) {
        locationApiService.getLocations({
            callback(it.locations)
        }, {
            callback(null)
        })
    }
}