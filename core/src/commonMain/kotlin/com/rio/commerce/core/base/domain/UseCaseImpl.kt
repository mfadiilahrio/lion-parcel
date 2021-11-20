package com.rio.commerce.core.base.domain

import com.badoo.reaktive.single.Single
import com.rio.commerce.core.data.Result
import com.rio.commerce.core.data.repository.Repository

class UseCaseImpl<R, T, Error>(
    private val repository: Repository<R, T, Error>
) : UseCase<R, T, Error> {

    override fun execute(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>> {
        return repository.get(request, loadCacheDuringRequest)
    }
}