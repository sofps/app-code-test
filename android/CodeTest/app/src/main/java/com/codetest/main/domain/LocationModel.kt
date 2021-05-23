package com.codetest.main.domain

import com.codetest.main.data.model.Status

interface LocationResult

data class LocationSuccess(
    val locations: List<Location>
) : LocationResult

data class Location(
    val id: String,
    val name: String?,
    val temperature: String?,
    val status: Status
)

data class LocationFailure(
    val error: String?
) : LocationResult

data class NewLocation(
    val id: String?,
    val name: String,
    val temperature: String,
    val status: Status
)
