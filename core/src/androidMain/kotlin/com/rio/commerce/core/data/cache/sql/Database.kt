package com.rio.commerce.core.data.cache.sql

import com.squareup.sqldelight.db.SqlDriver

actual fun getDriver(dbName: String): SqlDriver =
    throw UninitializedPropertyAccessException("Android SqlDriver must be provided from main app")
