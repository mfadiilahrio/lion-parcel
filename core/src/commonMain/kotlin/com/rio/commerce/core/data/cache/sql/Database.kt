package com.rio.commerce.core.data.cache.sql

import com.rio.commerce.core.sql.CommerceDatabase
import com.squareup.sqldelight.db.SqlDriver

expect fun getDriver(dbName: String): SqlDriver

class Database(dbName: String, sqlDriver: SqlDriver?) {
    val driver: SqlDriver = sqlDriver ?: getDriver(dbName)
    val database: CommerceDatabase = CommerceDatabase(driver)
}