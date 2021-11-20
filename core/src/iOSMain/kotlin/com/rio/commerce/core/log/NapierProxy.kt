package com.rio.commerce.core.log

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun debugBuild() {
    Napier.base(DebugAntilog())
}

fun releaseBuild(antilog: Antilog) {
    Napier.base(antilog)
}