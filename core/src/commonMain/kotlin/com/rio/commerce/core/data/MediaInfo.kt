package com.rio.commerce.core.data

import com.rio.commerce.core.data.model.Parcelable
import com.rio.commerce.core.data.model.Parcelize

@Parcelize
data class MediaInfo(
    val mediaType: String,
    val filename: String,
    val media: Media
) : Parcelable