package com.codetest.main.usecase

import com.codetest.main.data.repository.LocationRepository
import com.codetest.main.domain.NewLocation
import javax.inject.Inject

class AddLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(location: NewLocation) = locationRepository.addLocation(location)
}
