package com.codetest.main.data.repository

import com.codetest.main.data.api.LocationApiService
import com.codetest.main.domain.LocationFailure
import com.codetest.main.domain.LocationResult
import com.codetest.main.domain.LocationSuccess
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationApiService: LocationApiService
) {

    fun getLocations(): Observable<LocationResult> =
        locationApiService.getLocations()
            .map { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    LocationSuccess(body?.locations?.map { it.toDomain() } ?: emptyList())
                } else {
                    LocationFailure(response.errorBody()?.string())
                }
            }

}