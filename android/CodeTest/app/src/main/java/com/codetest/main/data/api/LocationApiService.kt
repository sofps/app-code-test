package com.codetest.main.data.api

import com.codetest.main.KeyUtil
import com.codetest.main.data.model.GetLocationsResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject
import javax.inject.Singleton

interface LocationApi {

    @GET("locations")
    fun getLocations(@Header("X-Api-Key") apiKey: String): Observable<Response<GetLocationsResponse>>
}

@Singleton
class LocationApiService @Inject constructor(
    retrofit: Retrofit,
    private val keyUtil: KeyUtil
) {
    private val api: LocationApi = retrofit.create(LocationApi::class.java)

    fun getLocations(): Observable<Response<GetLocationsResponse>> =
        api.getLocations(keyUtil.getKey())
}