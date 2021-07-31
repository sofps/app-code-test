package com.codetest.main.ui

sealed class WeatherForecastState {

    object Loading : WeatherForecastState()

    data class Success(
        val locations: List<LocationUI>,
        val clearAll: Boolean = true
    ): WeatherForecastState()

    object Error : WeatherForecastState()
}
