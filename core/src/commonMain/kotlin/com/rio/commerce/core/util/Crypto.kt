package com.rio.commerce.core.util

import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke
import io.ktor.utils.io.core.*
import kotlin.random.Random.Default.nextInt

fun getChipperKey(storageKey: String): ByteArray {
    val settings = Settings()

    val chipperKey = settings.getStringOrNull(storageKey)

    return if (chipperKey == null) {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        val randomString = (1..32)
            .map { i -> nextInt(i, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        settings.putString(storageKey, randomString)

        randomString.toByteArray()
    } else {
        settings.getString(storageKey).toByteArray()
    }
}
