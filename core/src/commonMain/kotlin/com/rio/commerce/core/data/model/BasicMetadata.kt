package com.rio.commerce.core.data.model

@Parcelize
data class BasicMetadata(
    override val pagination: Pagination?
) : Metadata, Parcelable