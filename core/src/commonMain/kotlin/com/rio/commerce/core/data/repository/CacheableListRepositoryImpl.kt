package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.flatMap
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.cache.Cache
import com.rio.commerce.core.data.model.ForceReloadableRequest
import com.rio.commerce.core.data.model.ListableData
import com.rio.commerce.core.data.model.ListableRequest

class CacheableListRepositoryImpl<R, T : ListableData<E>, E, Error>(
    private val service: Service<R, T, Error>,
    private val cacheService: Service<R, T, Error>,
    private val cache: Cache<R, E>,
    private val forceReloadFromCache: Boolean = false
) : Repository<R, T, Error> where R : ListableRequest, R : ForceReloadableRequest {

    override fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {

        if (request?.forceReload != true && cache.isCached(request) && !cache.isExpired(request) || loadCacheDuringRequest) {
            return cacheService.execute(request)
        }

        return service.execute(request).doOnBeforeSuccess {
            when (it) {
                is Result.Success -> {
                    if (request != null && request.page <= 1) {
                        cache.removeByRequest(request)
                    }

                    cache.putList(it.response.list)
                }
                else -> {
                }
            }
        }.run {
            if (forceReloadFromCache) {
                flatMap {
                    cacheService.execute(request)
                }
            } else {
                this
            }
        }
    }
}