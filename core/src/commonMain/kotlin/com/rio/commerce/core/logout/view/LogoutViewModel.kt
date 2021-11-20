package com.rio.commerce.core.logout.view

import com.russhwolf.settings.Settings
import com.russhwolf.settings.invoke

class LogoutViewModel {

    fun logout() {
        val settings = Settings()

        settings.clear()
    }
}