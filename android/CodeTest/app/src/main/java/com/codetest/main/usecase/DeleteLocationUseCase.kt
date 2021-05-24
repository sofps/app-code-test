package com.codetest.main.usecase

import com.codetest.main.data.repository.LocationRepository
import javax.inject.Inject

class DeleteLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {

    operator fun invoke(locationId: String) = locationRepository.deleteLocation(locationId)
}
