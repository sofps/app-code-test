package com.codetest.main.data.api

import com.codetest.main.data.model.AddLocationApiModel
import com.codetest.main.data.model.GetLocationsResponse
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import javax.inject.Inject
import javax.inject.Singleton

interface LocationApi {

    @GET("locations")
    fun getLocations(@Header("X-Api-Key") apiKey: String): Observable<Response<GetLocationsResponse>>

    @POST("locations")
    fun addLocation(
        @Header("X-Api-Key") apiKey: String,
        @Body location: AddLocationApiModel
    ): Observable<Response<AddLocationApiModel>>

    @DELETE("locations/{id}")
    fun deleteLocation(
        @Header("X-Api-Key") apiKey: String,
        @Path("id") locationId: String
    ): Completable
}

@Singleton
class LocationApiService @Inject constructor(
    retrofit: Retrofit,
    private val keyUtil: KeyUtil
) {
    private val api: LocationApi = retrofit.create(LocationApi::class.java)

    fun getLocations(): Observable<Response<GetLocationsResponse>> =
        api.getLocations(keyUtil.getKey())

    fun addLocation(location: AddLocationApiModel): Observable<Response<AddLocationApiModel>> =
        api.addLocation(keyUtil.getKey(), location)

    fun deleteLocation(locationId: String): Completable = api.deleteLocation(keyUtil.getKey(), locationId)
}