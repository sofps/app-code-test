package com.codetest.main.data.repository

import com.codetest.main.data.api.LocationApiService
import com.codetest.main.domain.*
import io.reactivex.Completable
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

    fun addLocation(location: NewLocation): Observable<LocationResult> =
        locationApiService.addLocation(location.toAddLocationApiModel())
            .map { response ->
                if (response.isSuccessful) {
                    val body = response.body()
                    val locations = body?.let {
                        listOf(
                            Location(
                                id = it.id!!,
                                name = it.name,
                                temperature = it.temperature,
                                status = it.status
                            )
                        )
                    } ?: emptyList()
                    LocationSuccess(locations)
                } else {
                    LocationFailure(response.errorBody()?.string())
                }
            }

    fun deleteLocation(locationId: String): Completable = locationApiService.deleteLocation(locationId)

}