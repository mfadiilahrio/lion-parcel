package com.rio.commerce.core.data.cache.sql

import com.rio.commerce.core.data.TimeInterval
import com.rio.commerce.core.data.cache.DataSource
import com.soywiz.klock.DateTime
import com.squareup.sqldelight.db.SqlCursor

abstract class SQLiteDataSource<R, T>(private val dbHelper: Database) : DataSource<R, T> {

    abstract val tableName: String

    abstract fun getSelectionArgs(request: R?): String
    abstract fun getItem(cursor: SqlCursor): T
    abstract fun insertModel(model: T, timestamp: TimeInterval): Long
    abstract fun insertRequest(request: R, timestamp: TimeInterval): Long

    override fun read(
        request: R?,
        selection: String?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): List<T> {

        var sqlSelection =
            if (selection == null) "WHERE $PRIMARY != 0" else "WHERE $PRIMARY != 0 $selection"

        sqlSelection += getSelectionArgs(request)

        val cursor = dbHelper.driver.executeQuery(null, "SELECT * FROM $tableName $sqlSelection", 0)

        val list = mutableListOf<T>()

        while (cursor.next()) {
            val model = getItem(cursor)

            list.add(model)
        }

        cursor.close()

        return list
    }

    override fun getTotal(
        request: R?,
        selection: String?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): Long {
        var additionalSelection = " WHERE $PRIMARY != 0"

        val count: Long

        if (selection != null) {
            additionalSelection += " AND $selection"
        }

        additionalSelection += getSelectionArgs(request)

        val cursor = dbHelper.driver.executeQuery(
            null,
            "SELECT COUNT(_id) FROM $tableName $additionalSelection",
            0
        )

        count = if (cursor.next()) {
            cursor.getLong(0) ?: 0
        } else {
            0
        }

        cursor.close()

        return count
    }

    override fun insertModel(model: T): Long {
        val timestamp = DateTime.nowUnix()

        var id: Long = 0

        dbHelper.database.transaction {
            id = insertModel(model, timestamp)

            if (id <= 0) {
                rollback()
            }
        }

        return id
    }

    override fun insertRequest(request: R): Long {
        val timestamp = DateTime.nowUnix()

        var id: Long = 0

        dbHelper.database.transaction {
            id = insertRequest(request, timestamp)

            if (id <= 0) {
                rollback()
            }
        }

        return id
    }

    override fun bulkInsert(list: List<T>): Boolean {
        val timestamp = DateTime.nowUnix()

        var isTransactionSuccess = true

        dbHelper.database.transaction {
            for (model in list) {
                isTransactionSuccess = insertModel(model, timestamp) > 0
            }

            if (!isTransactionSuccess) {
                rollback()
            }
        }

        return isTransactionSuccess
    }

    override fun deleteByRequest(request: R): Int {
        return 0
    }

    override fun deleteByModel(model: T): Int {
        return 0
    }

    override fun deleteByIds(request: List<Int>) {
        dbHelper.driver.execute(
            null,
            "DELETE FROM $tableName WHERE id IN (${request.joinToString(",")})",
            0
        )
    }

    override fun updateByModel(model: T): Int {
        return 0
    }

    override fun updateByRequest(request: R): Int {
        return 0
    }

    override fun removeAll() {
        dbHelper.driver.execute(null, "DELETE FROM $tableName", 0)
    }

    companion object {
        const val TIMESTAMP = "timestamp"
        const val PRIMARY = "_id"
        const val ID = "id"
    }
}
