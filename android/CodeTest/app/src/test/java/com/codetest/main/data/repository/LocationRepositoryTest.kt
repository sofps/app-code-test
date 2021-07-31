package com.codetest.main.data.repository

import com.codetest.main.data.api.LocationApiService
import com.codetest.main.data.model.AddLocationApiModel
import com.codetest.main.data.model.GetLocationsResponse
import com.codetest.main.data.model.LocationApiModel
import com.codetest.main.data.model.Status
import com.codetest.main.domain.Location
import com.codetest.main.domain.LocationFailure
import com.codetest.main.domain.LocationSuccess
import com.codetest.main.domain.NewLocation
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Observable
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LocationRepositoryTest {

    private val locationApiService: LocationApiService = mock()

    lateinit var locationRepository: LocationRepository

    @Before
    fun setup() {
        locationRepository = LocationRepository(locationApiService)
    }

    @Test
    fun `given a successful api request, when getting locations, success location result is returned`() {
        // Given
        val location = LocationApiModel(
            id = "123456",
            name = "Montevideo",
            temperature = "25",
            status = Status.SUNNY
        )
        val response = Response.success(GetLocationsResponse(listOf(location)))
        whenever(locationApiService.getLocations()) doReturn Observable.just(response)

        // When
        val actual = locationRepository.getLocations().test()

        // Then
        val expected = LocationSuccess(listOf(location.toDomain()))
        actual.assertValue(expected)
    }

    @Test
    fun `given a failed api request, when getting locations, failure location result is returned`() {
        // Given
        val response = Response.error<GetLocationsResponse>(500, "error".toResponseBody())
        whenever(locationApiService.getLocations()) doReturn Observable.just(response)

        // When
        val actual = locationRepository.getLocations().test()

        // Then
        val expected = LocationFailure("error")
        actual.assertValue(expected)
    }

    @Test
    fun `given a new location and a successful api request, when adding the new location, success location result is returned`() {
        // Given
        val newLocation = NewLocation(
            id = "123456",
            name = "Montevideo",
            temperature = "25",
            status = Status.SUNNY
        )
        val response = Response.success(newLocation.toAddLocationApiModel())
        whenever(locationApiService.addLocation(any())) doReturn Observable.just(response)

        // When
        val actual = locationRepository.addLocation(newLocation).test()

        // Then
        val expected = LocationSuccess(
            listOf(
                Location(
                    id = "123456",
                    name = "Montevideo",
                    temperature = "25",
                    status = Status.SUNNY
                )
            )
        )
        actual.assertValue(expected)
    }

    @Test
    fun `given a new location and a failed api request, when adding the new location, failure location result is returned`() {
        // Given
        val newLocation = NewLocation(
            id = "123456",
            name = "Montevideo",
            temperature = "25",
            status = Status.SUNNY
        )
        val response = Response.error<AddLocationApiModel>(500, "error".toResponseBody())
        whenever(locationApiService.addLocation(any())) doReturn Observable.just(response)

        // When
        val actual = locationRepository.addLocation(newLocation).test()

        // Then
        val expected = LocationFailure("error")
        actual.assertValue(expected)
    }

}