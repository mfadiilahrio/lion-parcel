package com.rio.commerce.core.data

import io.ktor.client.request.*
import io.ktor.http.*

abstract class CloudService<R, T, Error>(
    private val mHeaders: Map<String, String>,
    private val mHost: String
) : Service<R, T, Error> {

    fun HttpRequestBuilder.apiUrl(path: String? = null) {
        addDefaultHeaders(mHeaders)
        contentType(ContentType.Application.Json)

        url {
            takeFrom(mHost)
            path?.let {
                encodedPath = it
            }
        }
    }
}