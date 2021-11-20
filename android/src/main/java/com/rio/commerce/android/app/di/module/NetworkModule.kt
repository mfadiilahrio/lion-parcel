package com.rio.commerce.android.app.di.module

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.rio.commerce.android.app.BuildConfig
import com.rio.commerce.common.qualifier.Host
import com.rio.commerce.common.qualifier.HttpHeaders
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule(
    private val mApiHost: String,
    private val mContext: Context
) {

    @Provides
    @Singleton
    @Host
    fun provideHost(): String {
        return mApiHost
    }

    @SuppressLint("HardwareIds")
    @Provides
    @HttpHeaders
    fun provideHttpHeaders(): Map<String, String> {
        return mapOf(
            "Platform" to "Android",
            "Accept-Language" to "en",
            "User-Agent" to "android",
            "Build-Version" to BuildConfig.VERSION_NAME,
            "Build-Number" to BuildConfig.VERSION_CODE.toString(),
            "Device" to Settings.Secure.getString(
                mContext.contentResolver,
                Settings.Secure.ANDROID_ID
            ),
            "Device-Type" to Build.VERSION.RELEASE,
            "Device-Name" to Build.MODEL,
            "Model" to Build.PRODUCT
        )
    }
}