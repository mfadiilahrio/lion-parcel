package com.rio.commerce.core.data

import com.badoo.reaktive.coroutinesinterop.singleFromCoroutine
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.onErrorReturn
import com.rio.commerce.core.data.model.Session
import io.ktor.client.request.*
import io.ktor.http.*

abstract class OAuthCloudService<R, T, Error>(
    private val mHeaders: Map<String, String>,
    private val mHost: String
) : Service<R, T, Error> {

    abstract suspend fun getResult(request: R?): Result<T, Error>

    fun HttpRequestBuilder.apiUrl(path: String? = null) {

        val token = Session.token

        addDefaultHeaders(mHeaders)
        header(HttpHeaders.Host, Url(mHost).host)
        header(HttpHeaders.Authorization, "Bearer $token")

        url {
            takeFrom(mHost)
            path?.let {
                encodedPath = it
            }
        }
    }

    override fun execute(request: R?): Single<Result<T, Error>> {
        return singleFromCoroutine {
            getResult(request)

        }.onErrorReturn {
            Result.exception(it)
        }
    }
}