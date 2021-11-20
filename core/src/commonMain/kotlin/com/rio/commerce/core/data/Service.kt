package com.rio.commerce.core.data

import com.badoo.reaktive.single.Single
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

interface Service<in R, T, Error> {
    fun execute(request: R?): Single<Result<T, Error>>
}

val jsonConfiguration: Json by lazy {
    Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
}

@OptIn(InternalSerializationApi::class)
suspend inline fun <reified Response : Any, reified ErrorResponse : Any, Request, Model, Error> setResult(
    request: Request?,
    httpResponse: HttpResponse,
    mapper: Mapper<Request, Response, Model>,
    errorMapper: Mapper<Request, ErrorResponse, Error>
): Result<Model, Error> {
    val json = httpResponse.readText()

    return when (httpResponse.status) {
        HttpStatusCode.OK -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(Response::class.serializer(), json)

            Result.success(
                mapper.transform(request, parsedResponse)
            )
        }
        HttpStatusCode.Unauthorized -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(ErrorResponse::class.serializer(), json)

            Result.unauthorized(errorMapper.transform(request, parsedResponse))
        }
        HttpStatusCode.BadRequest, HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(ErrorResponse::class.serializer(), json)

            Result.fail(errorMapper.transform(request, parsedResponse))
        }
        else -> {
            throw ClientRequestException(httpResponse)
        }
    }
}

@OptIn(InternalSerializationApi::class)
suspend inline fun <reified Response : Any, reified ErrorResponse : Any, Request, Model, Error> setResultList(
    request: Request?,
    httpResponse: HttpResponse,
    mapper: Mapper<Request, List<Response>, Model>,
    errorMapper: Mapper<Request, ErrorResponse, Error>
): Result<Model, Error> {
    val json = httpResponse.readText()

    return when (httpResponse.status) {
        HttpStatusCode.OK -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(ListSerializer(Response::class.serializer()), json)

            Result.success(
                mapper.transform(request, parsedResponse)
            )
        }
        HttpStatusCode.Unauthorized -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(ErrorResponse::class.serializer(), json)

            Result.unauthorized(errorMapper.transform(request, parsedResponse))
        }
        HttpStatusCode.BadRequest, HttpStatusCode.NotFound, HttpStatusCode.Forbidden -> {
            val parsedResponse =
                jsonConfiguration.decodeFromString(ErrorResponse::class.serializer(), json)

            Result.fail(errorMapper.transform(request, parsedResponse))
        }
        else -> {
            throw ClientRequestException(httpResponse)
        }
    }
}

fun HttpRequestBuilder.addDefaultHeaders(httpHeaders: Map<String, String>) {

    httpHeaders.map {
        header(it.key, it.value)
    }
}
