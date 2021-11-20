package com.rio.commerce.core.base.http

import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.util.*

@OptIn(KtorExperimentalAPI::class)
actual val httpClient: HttpClient = HttpClient(Android) {
    expectSuccess = false

    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    engine {
        connectTimeout = 100_000
        socketTimeout = 100_000
    }
    install(Logging) {
        logger = AndroidLogger()
        level = LogLevel.ALL
    }
}

class AndroidLogger : Logger {
    override fun log(message: String) {
        Napier.log(Napier.Level.DEBUG, "RIO-APP", null, message)
    }
}