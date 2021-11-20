package com.rio.commerce.core.data.model

data class Loading(
    val start: Boolean,
    val hideRefreshControlAfterFinished: Boolean = true,
    val hideRefreshControlDuringLoading: Boolean = false
)