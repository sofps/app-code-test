package com.codetest.main.di

import android.content.Context
import com.codetest.main.data.api.KeyUtil
import com.codetest.main.data.api.LocationApiService
import com.codetest.main.util.BaseSchedulerProvider
import com.codetest.main.util.SchedulerProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://app-code-test.kry.pet/"

@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    @Provides
    fun provideGson(): Gson =
        GsonBuilder()
            .setLenient()
            .create()

    @Provides
    fun provideRetrofit(
        client: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    fun provideKeyUtil(@ApplicationContext context: Context): KeyUtil =
        KeyUtil(context)

    @Provides
    fun provideLocationApiService(
        retrofit: Retrofit,
        keyUtil: KeyUtil
    ): LocationApiService = LocationApiService(retrofit, keyUtil)

    @Provides
    fun provideSchedulerProvider(): BaseSchedulerProvider = SchedulerProvider()

}
