package com.rio.commerce.core.data.cache

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleOf
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.Service
import com.rio.commerce.core.data.model.*

class PageableListCacheService<R, T : ListableData<E>, E : Pageable, Error>(
    private val cache: Cache<R, E>
) : Service<R, T, Error> {

    @Suppress("UNCHECKED_CAST")
    override fun execute(request: R?): Single<Result<T, Error>> {
        val list = cache.getList(request)

        return if (!list.isNullOrEmpty()) {
            val last = list.last()

            val pagination = Pagination(
                last.pagination?.currentPage ?: 1,
                last.pagination?.itemsPerPage ?: 1,
                last.pagination?.totalPages ?: 1,
                last.pagination?.itemCount ?: 0,
                last.pagination?.totalItems ?: 0
            )

            val model = DataList(list, BasicMetadata(pagination)) as T
            val error = DataError(200, "") as Error

            singleOf(Result.success(model))
        } else {
            singleOf(Result.empty())
        }
    }
}