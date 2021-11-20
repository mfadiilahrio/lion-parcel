package com.rio.commerce.core.data.cache

import com.rio.commerce.core.data.cache.sql.Database
import com.rio.commerce.core.data.cache.sql.SQLiteDataSource
import com.rio.commerce.core.data.model.ListRequest
import com.squareup.sqldelight.db.SqlCursor

class DeleteItemSqlDataSource<T>(dbHelper: Database, name: String) :
    SQLiteDataSource<ListRequest, T>(dbHelper) {
    override val tableName: String = name

    override fun getSelectionArgs(request: ListRequest?): String {
        return ""
    }

    override fun getItem(cursor: SqlCursor): T {
        throw Throwable("Only for deletion")
    }

    override fun insertModel(model: T, timestamp: Double): Long {
        throw Throwable("Only for deletion")
    }

    override fun insertRequest(request: ListRequest, timestamp: Double): Long {
        throw Throwable("Only for deletion")
    }
}