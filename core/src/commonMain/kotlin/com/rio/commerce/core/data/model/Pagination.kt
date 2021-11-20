package com.rio.commerce.core.data.model

@Parcelize
data class Pagination(
    val currentPage: Int,
    val itemsPerPage: Int,
    val totalPages: Int,
    val itemCount: Int,
    val totalItems: Int
) : Parcelable