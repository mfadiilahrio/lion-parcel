package com.rio.commerce.android.app.di.module

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rio.commerce.core.data.cache.sql.Database
import com.rio.commerce.core.sql.CommerceDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CacheModule(private val mDbName: String) {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): Database {
        val driver = AndroidSqliteDriver(
            schema = CommerceDatabase.Schema,
            context = context,
            name = mDbName,
            callback = object : AndroidSqliteDriver.Callback(CommerceDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            })

        return Database(mDbName, driver)
    }

    @Provides
    @Singleton
    fun provideCache(driver: SqlDriver): CommerceDatabase {
        return CommerceDatabase(driver)
    }
}