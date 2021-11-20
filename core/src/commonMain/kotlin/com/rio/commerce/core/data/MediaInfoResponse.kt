package com.rio.commerce.core.data

import kotlinx.serialization.Serializable

@Serializable
data class MediaInfoResponse(
    val mediaType: String,
    val url: String,
    val filename: String
)