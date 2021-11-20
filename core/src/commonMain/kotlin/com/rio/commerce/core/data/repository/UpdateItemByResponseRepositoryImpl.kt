package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.cache.Cache
import com.rio.commerce.core.data.model.ListableRequest

class UpdateItemByResponseRepositoryImpl<R : ListableRequest, T, Error>(
    private val service: Service<R, T, Error>,
    private val cache: Cache<R, T>
) : Repository<R, T, Error> {

    override fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {
        return service.execute(request).doOnBeforeSuccess {
            when (it) {
                is Result.Success -> cache.updateByModel(it.response)
                else -> {
                }
            }
        }
    }
}