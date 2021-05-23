package com.codetest.main.ui

sealed class WeatherForecastState {

    object Loading : WeatherForecastState()

    data class Success(
        val locations: List<LocationUI>
    ): WeatherForecastState()

    object Error : WeatherForecastState()
}
