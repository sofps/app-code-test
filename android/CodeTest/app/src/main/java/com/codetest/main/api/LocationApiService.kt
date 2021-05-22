package com.codetest.main.api

import com.codetest.main.KeyUtil
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Header
import javax.inject.Inject
import javax.inject.Singleton

interface LocationApi {

    @GET("locations")
    fun getLocations(@Header("X-Api-Key") apiKey: String): Observable<JsonObject>
}

@Singleton
class LocationApiService @Inject constructor(
    retrofit: Retrofit,
    private val keyUtil: KeyUtil
) {
    private val api: LocationApi = retrofit.create(LocationApi::class.java)

    fun getLocations(success: (JsonObject) -> Unit, error: (String?) -> Unit) {
        api.getLocations(keyUtil.getKey())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    success(it)
                },
                onError = {
                    error(it.message)
                }
            )
    }
}