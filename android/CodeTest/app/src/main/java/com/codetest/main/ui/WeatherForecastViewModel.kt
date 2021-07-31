package com.codetest.main.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetest.main.domain.LocationSuccess
import com.codetest.main.domain.NewLocation
import com.codetest.main.usecase.AddLocationUseCase
import com.codetest.main.usecase.DeleteLocationUseCase
import com.codetest.main.usecase.GetLocationsUseCase
import com.codetest.main.util.BaseSchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val addLocationUseCase: AddLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase,
    private val schedulerProvider: BaseSchedulerProvider
) : ViewModel() {

    val weatherForecastLiveData = MutableLiveData<WeatherForecastState>()
    val addLocationLiveData = MutableLiveData<AddLocationState>()
    val deleteLocationLiveData = MutableLiveData<DeleteLocationState>()

    init {
        loadLocations()
    }

    fun addLocation(name: String, status: String, temperature: String) {
        viewModelScope.launch {
            addLocationLiveData.value = AddLocationState.Loading
            val location = NewLocation(
                name = name,
                temperature = temperature,
                status = StatusUI.from(status).toStatus()
            )
            addLocationUseCase(location)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeBy(
                    onNext = { result ->
                        if (result is LocationSuccess) {
                            addLocationLiveData.value = AddLocationState.Success
                            // To avoid an extra request to get the list of locations again
                            // we push the new location to weatherForecastLiveData with clearAll=false
                            // so it's added at the bottom of the list
                            weatherForecastLiveData.value =
                                WeatherForecastState.Success(
                                    result.locations.map { it.toLocationUI() },
                                    clearAll = false
                                )
                        } else {
                            addLocationLiveData.value = AddLocationState.Error
                        }
                    },
                    onError = {
                        addLocationLiveData.value = AddLocationState.Error
                    }
                )

        }
    }

    fun removeLocation(position: Int, location: LocationUI) {
        viewModelScope.launch {
            deleteLocationLiveData.value = DeleteLocationState.Loading
            deleteLocationUseCase(location.id)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeBy(
                    onComplete = {
                        deleteLocationLiveData.value = DeleteLocationState.Success
                    },
                    onError = {
                        deleteLocationLiveData.value = DeleteLocationState.Error(position, location)
                    }
                )
        }
    }

    fun loadLocations() {
        viewModelScope.launch {
            getLocationsUseCase()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeBy(
                    onNext = { result ->
                        if (result is LocationSuccess) {
                            weatherForecastLiveData.value =
                                WeatherForecastState.Success(result.locations.map { it.toLocationUI() })
                        } else {
                            weatherForecastLiveData.value = WeatherForecastState.Error
                        }
                    },
                    onError = {
                        weatherForecastLiveData.value = WeatherForecastState.Error
                    }
                )
        }
    }
}
