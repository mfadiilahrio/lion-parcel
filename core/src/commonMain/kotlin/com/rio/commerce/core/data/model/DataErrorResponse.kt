package com.rio.commerce.core.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DataErrorResponse(
    val status: Int,
    val message: String
)