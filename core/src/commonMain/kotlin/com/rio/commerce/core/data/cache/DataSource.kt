package com.rio.commerce.core.data.cache

interface DataSource<in R, T> {

    fun read(
        request: R?,
        selection: String?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): List<T>

    fun getTotal(
        request: R?,
        selection: String?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): Long

    fun insertModel(model: T): Long

    fun insertRequest(request: R): Long

    fun bulkInsert(list: List<T>): Boolean

    fun deleteByModel(model: T): Int

    fun deleteByRequest(request: R): Int

    fun deleteByIds(request: List<Int>)

    fun updateByModel(model: T): Int

    fun updateByRequest(request: R): Int

    fun removeAll()
}