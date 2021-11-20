package com.rio.commerce.core.base

import com.rio.commerce.core.base.base64.Base64Factory
import io.ktor.utils.io.core.*
import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun testEncodedString() {
        assertEquals(
            "YW5kcm9pZDpzZWNyZXQ=",
            Base64Factory.createEncoder().encodeToString("android:secret".toByteArray())
        )
    }
}