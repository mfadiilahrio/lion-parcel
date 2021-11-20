package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service

class RepositoryImpl<R, T, Error>(private val service: Service<R, T, Error>) :
    Repository<R, T, Error> {

    override fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {
        return service.execute(request)
    }
}