package com.rio.commerce.core.fact.data

import com.rio.commerce.core.base.http.httpClient
import com.rio.commerce.core.data.Mapper
import com.rio.commerce.core.data.OAuthCloudService
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.model.DataErrorResponse
import com.rio.commerce.core.data.setResult
import io.ktor.client.request.*
import io.ktor.client.statement.*

class GetFactCloudService<T, Error>(
    headers: Map<String, String>,
    host: String,
    private val path: String,
    private val mapper: Mapper<Any, FactResponse.Fact, T>,
    private val errorMapper: Mapper<Any, DataErrorResponse, Error>
) : OAuthCloudService<Nothing, T, Error>(headers, host) {

    override suspend fun getResult(request: Nothing?): Result<T, Error> {
        val httpResponse = httpClient.get<HttpResponse> {
            apiUrl(path)
        }

        return setResult(request, httpResponse, mapper, errorMapper)
    }
}