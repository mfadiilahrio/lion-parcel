package com.rio.commerce.core.data.cache

import com.rio.commerce.core.data.cache.sql.SQLiteDataSource.Companion.TIMESTAMP
import com.soywiz.klock.DateTime

class CacheImpl<in R, E>(
    private val dataSource: DataSource<R, E>,
    private val expirationDate: Expire
) : Cache<R, E> {

    override fun get(request: R?): E? {
        val models = dataSource.read(request, null, null, null, null)

        return if (models.size == 1) models[0] else null
    }

    override fun getList(request: R?): List<E>? {
        return dataSource.read(request, null, null, null, null)
    }

    override fun putModel(model: E) {
        dataSource.insertModel(model)
    }

    override fun putRequest(request: R) {
        dataSource.insertRequest(request)
    }

    override fun putList(models: List<E>) {
        dataSource.bulkInsert(models)
    }

    override fun updateByModel(model: E) {
        dataSource.updateByModel(model)
    }

    override fun updateByRequest(request: R) {
        dataSource.updateByRequest(request)
    }

    override fun isCached(request: R?): Boolean {
        val total = dataSource.getTotal(request, null, null, null, null)

        return total > 0
    }

    override fun isExpired(request: R?): Boolean {
        val currentTimestamp = DateTime.nowUnix()

        val total = dataSource.getTotal(
            request,
            TIMESTAMP + " > " + (currentTimestamp - expirationDate.time),
            null,
            null,
            null
        )

        return total < 1
    }

    override fun removeByModel(model: E) {
        dataSource.deleteByModel(model)
    }

    override fun removeByRequest(request: R) {
        dataSource.deleteByRequest(request)
    }

    override fun removeByIds(request: List<Int>) {
        dataSource.deleteByIds(request)
    }

    override fun removeAll() {
        dataSource.removeAll()
    }
}
