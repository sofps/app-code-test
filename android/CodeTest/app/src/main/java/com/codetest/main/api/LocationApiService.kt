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
import retrofit2.http.Url
import javax.inject.Inject
import javax.inject.Singleton

interface LocationApi {
    @GET
    fun get(@Header("X-Api-Key") apiKey: String, @Url url: String): Observable<JsonObject>
}

@Singleton
class LocationApiService @Inject constructor(
    retrofit: Retrofit,
    private val keyUtil: KeyUtil
) {
    private val api: LocationApi = retrofit.create(LocationApi::class.java)

    fun get(url: String, success: (JsonObject) -> Unit, error: (String?) -> Unit) {
        api.get(keyUtil.getKey(), url)
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