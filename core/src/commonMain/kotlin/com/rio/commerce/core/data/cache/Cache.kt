package com.rio.commerce.core.data.cache

interface Cache<in R, E> {

    fun get(request: R?): E?

    fun getList(request: R?): List<E>?

    fun putModel(model: E)

    fun putRequest(request: R)

    fun putList(models: List<E>)

    fun updateByModel(model: E)

    fun updateByRequest(request: R)

    fun isCached(request: R?): Boolean

    fun isExpired(request: R?): Boolean

    fun removeByModel(model: E)

    fun removeByRequest(request: R)

    fun removeByIds(request: List<Int>)

    fun removeAll()
}

enum class Expire(val time: Double) {
    NOW(0.0),
    ONE_DAY(86400000.0),
    ONE_WEEK(604800000.0),
    ONE_MONTH(2592000000.0)
}