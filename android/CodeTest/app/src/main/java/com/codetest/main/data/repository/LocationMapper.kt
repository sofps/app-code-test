package com.codetest.main.data.repository

import com.codetest.main.data.model.LocationApiModel
import com.codetest.main.domain.Location

fun LocationApiModel.toDomain() = Location(
    id = id,
    name = name,
    temperature = temperature,
    status = status
)
