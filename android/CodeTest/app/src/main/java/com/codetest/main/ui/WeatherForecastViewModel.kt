package com.codetest.main.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codetest.main.domain.LocationSuccess
import com.codetest.main.usecase.GetLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(
    private val getLocationsUseCase: GetLocationsUseCase
) : ViewModel() {

    val weatherForecastLiveData = MutableLiveData<WeatherForecastState>()

    init {
        initView()
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
