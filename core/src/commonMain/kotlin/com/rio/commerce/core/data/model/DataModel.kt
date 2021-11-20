package com.rio.commerce.core.data.model

data class DataModel<R, T>(
    val request: R,
    val item: T,
    val isFromInitialCache: Boolean,
    val metadata: Metadata?
)