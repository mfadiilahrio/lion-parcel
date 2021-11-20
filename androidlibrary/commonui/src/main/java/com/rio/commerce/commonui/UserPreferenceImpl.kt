package com.rio.commerce.commonui

import android.content.Context
import com.rio.commerce.core.UserPreference

class UserPreferenceImpl(context: Context, name: String) : UserPreference {
    private val mPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override val isLoggedIn: Boolean
        get() = !mPrefs.getString(UserPreference.userEmail, null).isNullOrEmpty()

    override var userEmail: String?
        get() = mPrefs.getString(UserPreference.userEmail, null)
        set(value) {
            mPrefs.edit().apply {
                putString(UserPreference.userEmail, value)
                apply()
            }
        }

    override var deviceId: String?
        get() = mPrefs.getString(UserPreference.deviceId, null)
        set(value) {
            mPrefs.edit().apply {
                putString(UserPreference.deviceId, value)
                apply()
            }
        }

    override fun clear() {
        userEmail = null
//        mPrefs.edit().clear().apply()
    }
}