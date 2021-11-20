package com.rio.commerce.core.data.cache.sql

import co.touchlab.sqliter.DatabaseConfiguration
import com.rio.commerce.core.sql.CommerceDatabase.Companion.Schema
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import com.squareup.sqldelight.drivers.native.wrapConnection

actual fun getDriver(dbName: String): SqlDriver {
    val config = DatabaseConfiguration(
        dbName,
        Schema.version,
        foreignKeyConstraints = true,
        create = { connection ->
            wrapConnection(connection) { Schema.create(it) }
        },
        upgrade = { connection, oldVersion, newVersion ->
            wrapConnection(connection) { Schema.migrate(it, oldVersion, newVersion) }
        }
    )
    return NativeSqliteDriver(config)
}