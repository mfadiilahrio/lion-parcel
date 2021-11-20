package com.rio.commerce.core.data.model

import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class DataError(
    val status: Int,
    val message: String
) : Parcelable