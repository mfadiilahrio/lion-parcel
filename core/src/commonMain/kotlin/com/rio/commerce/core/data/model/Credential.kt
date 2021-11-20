package com.rio.commerce.core.data.model

import com.rio.commerce.core.data.cache.CacheKey
import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke

interface Credential {
    val token: String
}

object Session {
    private val mSettings = Settings()

    val hasToken: Boolean
        get() {
            return mSettings.getStringOrNull(CacheKey.TOKEN) != null
        }

    val token: String
        get() {
            return mSettings.getString(CacheKey.TOKEN)
        }

    fun save(auth: Credential) {
        mSettings.putString(CacheKey.TOKEN, auth.token)
    }

    fun clear() {
        mSettings.remove(CacheKey.TOKEN)
    }
}