package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.cache.Cache
import com.rio.commerce.core.data.model.ForceReloadableRequest

class CacheableRepositoryImpl<R : ForceReloadableRequest, T, Error>(
    private val service: Service<R, T, Error>,
    private val cacheService: Service<R, T, Error>,
    private val cache: Cache<R, T>
) : Repository<R, T, Error> {

    override fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {

        if (request?.forceReload != true && cache.isCached(request) && !cache.isExpired(request)) {
            return cacheService.execute(request)
        }

        return service.execute(request).doOnBeforeSuccess {
            when (it) {
                is Result.Success -> {
                    cache.removeByModel(it.response)
                    cache.putModel(it.response)
                }
                else -> {
                }
            }
        }
    }
}