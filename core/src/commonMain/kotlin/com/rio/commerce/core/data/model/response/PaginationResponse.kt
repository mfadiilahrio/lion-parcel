package com.rio.commerce.core.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PaginationResponse(
    val currentPage: Int,
    val itemsPerPage: Int,
    val totalPages: Int,
    val itemCount: Int,
    val totalItems: Int
)