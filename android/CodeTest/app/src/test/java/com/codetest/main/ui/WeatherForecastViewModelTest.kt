package com.codetest.main.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.codetest.main.data.model.Status
import com.codetest.main.domain.Location
import com.codetest.main.domain.LocationSuccess
import com.codetest.main.usecase.AddLocationUseCase
import com.codetest.main.usecase.DeleteLocationUseCase
import com.codetest.main.usecase.GetLocationsUseCase
import com.codetest.util.TestLifecycleOwner
import com.codetest.util.TestSchedulerProvider
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class WeatherForecastViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val getLocationsUseCase: GetLocationsUseCase = mock()
    private val addLocationUseCase: AddLocationUseCase = mock()
    private val deleteLocationUseCase: DeleteLocationUseCase = mock()

    private val weatherForecastObserver: Observer<WeatherForecastState> = mock()
    private val addLocationObserver: Observer<AddLocationState> = mock()

    private lateinit var testLifecycleOwner: TestLifecycleOwner
    private lateinit var viewModel: WeatherForecastViewModel

    init {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Before
    fun setup() {
        testLifecycleOwner = TestLifecycleOwner()
        testLifecycleOwner.onCreate()

        whenever(getLocationsUseCase()) doReturn Observable.empty()

        viewModel = WeatherForecastViewModel(
            getLocationsUseCase,
            addLocationUseCase,
            deleteLocationUseCase,
            TestSchedulerProvider()
        )

        viewModel.weatherForecastLiveData.observe(testLifecycleOwner, weatherForecastObserver)
        viewModel.addLocationLiveData.observe(testLifecycleOwner, addLocationObserver)
    }

    @After
    fun tearDown() {
        testLifecycleOwner.onDestroy()
    }

    @Test
    fun `given a list of locations, when loading locations, success state with locations is posted`() = runBlockingTest {
        // Given
        testLifecycleOwner.onResume()
        val location = Location(
            id = "123456",
            name = "Montevideo",
            temperature = "25",
            status = Status.SUNNY
        )
        whenever(getLocationsUseCase()) doReturn Observable.just(LocationSuccess(listOf(location)))

        // When
        viewModel.loadLocations()

        // Then
        val expected = WeatherForecastState.Success(listOf(location.toLocationUI()))
        verify(weatherForecastObserver).onChanged(expected)
    }

    @Test
    fun `given a new location and a successful request, when adding a new location, new location is posted`() = runBlockingTest {
        // Given
        testLifecycleOwner.onResume()
        val name = "Montevideo"
        val status = Status.SUNNY
        val temperature = "25"
        val location = Location(
            id = "123456",
            name = name,
            temperature = temperature,
            status = Status.SUNNY
        )
        whenever(addLocationUseCase(any())) doReturn Observable.just(LocationSuccess(listOf(location)))

        // When
        viewModel.addLocation(name, status.toString(), temperature)

        // Then
        verify(addLocationObserver).onChanged(AddLocationState.Loading)
        verify(addLocationObserver).onChanged(AddLocationState.Success)

        val expected = WeatherForecastState.Success(listOf(location.toLocationUI()), clearAll = false)
        verify(weatherForecastObserver).onChanged(expected)
    }
}
