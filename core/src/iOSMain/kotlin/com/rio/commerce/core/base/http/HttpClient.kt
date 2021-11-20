package com.rio.commerce.core.base.http

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*

actual val httpClient: HttpClient = HttpClient {
    expectSuccess = false

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = if (Platform.isDebugBinary) {
            LogLevel.ALL
        } else {
            LogLevel.NONE
        }
    }
}
