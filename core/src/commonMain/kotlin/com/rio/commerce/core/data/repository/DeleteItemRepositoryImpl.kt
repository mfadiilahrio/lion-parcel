package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.cache.Cache
import com.rio.commerce.core.data.model.ListableRequest
import com.rio.commerce.core.exception.InvalidRequestException

class DeleteItemRepositoryImpl<R : ListableRequest, T, Error>(
    private val service: Service<R, T, Error>,
    private val cache: Cache<R, T>
) : Repository<R, T, Error> {

    override fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {
        val deleteRequest = request?.id ?: throw InvalidRequestException()

        return service.execute(request).doOnBeforeSuccess {
            when (it) {
                is Result.Success -> {
                    cache.removeByIds(listOf(deleteRequest))
                }
                else -> {
                }
            }
        }
    }
}