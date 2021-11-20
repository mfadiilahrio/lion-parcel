package com.rio.commerce.core.base.base64

import android.util.Base64

actual object Base64Factory {
    actual fun createEncoder(): Base64Encoder = AndroidBase64Encoder
}

object AndroidBase64Encoder : Base64Encoder {
    override fun encode(src: ByteArray): ByteArray = Base64.encode(src, Base64.NO_WRAP)
    override fun encodeToString(src: ByteArray): String = Base64.encodeToString(src, Base64.NO_WRAP)
}