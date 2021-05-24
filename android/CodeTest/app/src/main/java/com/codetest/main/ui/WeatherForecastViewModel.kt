package com.codetest.main.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetest.main.domain.LocationSuccess
import com.codetest.main.domain.NewLocation
import com.codetest.main.usecase.AddLocationUseCase
import com.codetest.main.usecase.DeleteLocationUseCase
import com.codetest.main.usecase.GetLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase,
    private val addLocationUseCase: AddLocationUseCase,
    private val deleteLocationUseCase: DeleteLocationUseCase
) : ViewModel() {

    val weatherForecastLiveData = MutableLiveData<WeatherForecastState>()
    val addLocationLiveData = MutableLiveData<AddLocationState>()
    val deleteLocationLiveData = MutableLiveData<DeleteLocationState>()

    init {
        initView()
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { result ->
                        if (result is LocationSuccess) {
                            addLocationLiveData.value = AddLocationState.Success
                            initView()
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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

    private fun initView() {
        viewModelScope.launch {
            getLocationsUseCase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
