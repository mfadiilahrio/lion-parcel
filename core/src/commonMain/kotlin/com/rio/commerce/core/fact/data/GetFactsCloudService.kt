package com.rio.commerce.core.fact.data

import com.rio.commerce.core.base.http.httpClient
import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.OAuthCloudService
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.model.DataErrorResponse
import com.rio.commerce.core.data.model.ListRequest
import com.rio.commerce.core.data.setResult
import com.rio.commerce.core.exception.InvalidRequestException
import io.ktor.client.request.*
import io.ktor.client.statement.*

class GetFactsCloudService<T, Error>(
    headers: Map<String, String>,
    host: String,
    private val path: String,
    private val mapper: Mapper<Any, FactsResponse, T>,
    private val errorMapper: Mapper<Any, DataErrorResponse, Error>
) : OAuthCloudService<ListRequest, T, Error>(headers, host) {

    override suspend fun getResult(request: ListRequest?): Result<T, Error> {
        val req = request ?: throw InvalidRequestException()

        val httpResponse = httpClient.get<HttpResponse> {
            apiUrl(path)
            with(req) {
                parameter("page", page)
            }
        }

        return setResult(request, httpResponse, mapper, errorMapper)
    }
}