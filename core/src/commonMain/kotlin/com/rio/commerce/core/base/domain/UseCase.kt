package com.rio.commerce.core.base.domain

import com.badoo.reaktive.single.Single
import com.rio.commerce.core.data.Result

interface UseCase<in R, T, Error> {
    fun execute(request: R?, loadCacheDuringRequest: Boolean = false): Single<Result<T, Error>>
}