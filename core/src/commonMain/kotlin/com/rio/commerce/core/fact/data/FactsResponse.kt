package com.rio.commerce.core.fact.data

import com.rio.commerce.core.data.model.Response
import kotlinx.serialization.Serializable

@Serializable
data class FactsResponse(
    val current_page: Int,
    val data: List<FactResponse.Fact>,
    val last_page: Int,
    val per_page: Int,
    val total: Int
) : Response

@Serializable
data class FactResponse(val data: Fact) {

    @Serializable
    data class Fact(
        val fact: String,
        val length: Int
    )
}