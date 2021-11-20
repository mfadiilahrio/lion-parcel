package com.rio.commerce.core.data.cache

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleOf
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.model.DataError
import com.rio.commerce.core.data.model.DataList
import com.rio.commerce.core.data.model.ListableData

class ListCacheService<R, T : ListableData<E>, E, Error>(
    private val cache: Cache<R, E>
) : Service<R, T, Error> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(request: R?): Single<Result<T, Error>> {
        val list = cache.getList(request)

        return if (!list.isNullOrEmpty()) {
            val model = DataList(list, null) as T
            val error = DataError(200, "") as Error

            singleOf(Result.success(model))
        } else {
            singleOf(Result.empty())
        }
    }
}