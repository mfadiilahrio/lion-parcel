package com.rio.commerce.core.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MetadataResponse(
    val filters: List<FilterResponse>? = null,
    val pagination: PaginationResponse? = null,
    val currentPage: Int? = null,
    val itemsPerPage: Int? = null,
    val totalPages: Int? = null,
    val itemCount: Int? = null,
    val totalItems: Int? = null
) {

    @Serializable
    data class FilterResponse(
        val name: String,
        val values: List<Value>?
    ) {

        @Serializable
        data class Value(
            val count: Int,
            val key: String
        )

    }

}