package com.rio.commerce.core.fact.data

import com.rio.commerce.core.data.model.Pageable
import com.rio.commerce.core.data.model.Pagination
import com.rio.commerce.core.data.model.Parcelable
import com.rio.commerce.core.data.model.Parcelize

@Parcelize
data class Fact(
    val fact: String,
    val length: Int,
    override val pagination: Pagination?
) : Parcelable, Pageable