package com.rio.commerce.core.util

import java.io.File

actual fun readFile(uri: String): ByteArray = File(uri).readBytes()