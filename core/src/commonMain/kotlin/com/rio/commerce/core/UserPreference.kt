package com.rio.commerce.core

interface UserPreference {
    val isLoggedIn: Boolean

    var userEmail: String?

    var deviceId: String?

    fun clear()

    companion object {
        const val userEmail = "prefs.userEmail"
        const val deviceId = "prefs.deviceId"
    }
}