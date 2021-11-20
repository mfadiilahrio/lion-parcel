package com.rio.commerce.core.data.repository

import com.badoo.reaktive.single.Single
import com.rio.commerce.core.data.Result

interface Repository<in R, T, Error> {
    fun get(request: R?, loadCacheDuringRequest: Boolean): Single<Result<T, Error>>
}