package com.rio.commerce.core.fact.data.cache

import com.rio.commerce.core.data.cache.sql.Database
import com.rio.commerce.core.data.cache.sql.SQLiteDataSource
import com.rio.commerce.core.data.model.ListRequest
import com.rio.commerce.core.data.model.Pagination
import com.rio.commerce.core.fact.data.Fact
import com.squareup.sqldelight.db.SqlCursor

class FactSqlDataSource(private val dbHelper: Database) :
    SQLiteDataSource<ListRequest, Fact>(dbHelper) {
    companion object {
        const val TABLE_NAME = "factRecord"
    }

    override val tableName: String = TABLE_NAME
    override fun getSelectionArgs(request: ListRequest?): String {
        val page = request?.page ?: 1
        val ignorePaging = request?.ignorePaging ?: false

        return if (page != 1 && !ignorePaging) {
            " AND currentPage = $page"
        } else {
            ""
        }
    }

    override fun read(
        request: ListRequest?,
        selection: String?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): List<Fact> {
        val page = request?.page ?: 1
        val ignorePaging = request?.ignorePaging ?: false

        val list = if (page > 1 && !ignorePaging) {
            dbHelper.database.factQueries.selectByPage(page).executeAsList()
        } else {
            dbHelper.database.factQueries.selectAll().executeAsList()
        }

        return list.map {
            Fact(
                it.fact,
                it.length,
                Pagination(
                    it.currentPage,
                    it.itemsPerPage,
                    it.totalPages,
                    it.itemCount,
                    it.totalItems
                )
            )
        }
    }

    override fun getItem(cursor: SqlCursor): Fact {
        throw Throwable()
    }

    override fun insertModel(model: Fact, timestamp: Double): Long {
        dbHelper.database.factQueries.insert(
            model.fact,
            model.length,
            model.pagination?.currentPage ?: 1,
            model.pagination?.itemsPerPage ?: 1,
            model.pagination?.totalPages ?: 1,
            model.pagination?.itemCount ?: 0,
            model.pagination?.totalItems ?: 0,
            timestamp
        )
        return dbHelper.database.commonQueries.rowid().executeAsOne()
    }

    override fun insertRequest(request: ListRequest, timestamp: Double): Long {
        return 0
    }

    override fun deleteByRequest(request: ListRequest): Int {
        removeAll()
        return 0
    }
}