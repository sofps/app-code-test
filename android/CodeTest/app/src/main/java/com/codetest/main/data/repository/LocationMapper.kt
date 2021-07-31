package com.codetest.main.data.repository

import com.codetest.main.data.model.AddLocationApiModel
import com.codetest.main.data.model.LocationApiModel
import com.codetest.main.domain.Location
import com.codetest.main.domain.NewLocation

fun LocationApiModel.toDomain() = Location(
    id = id,
    name = name,
    temperature = temperature,
    status = status
)

fun NewLocation.toAddLocationApiModel() = AddLocationApiModel(
    id = id,
    name = name,
    temperature = temperature,
    status = status
)
