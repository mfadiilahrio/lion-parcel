package com.rio.commerce.android.app.di.module

import android.content.Context
import com.rio.commerce.android.app.Application
import com.rio.commerce.android.app.R
import dagger.Module
import dagger.Provides
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import java.util.*
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application
    }

    @Singleton
    @Provides
    fun provideLibPhoneNumber(application: Application): PhoneNumberUtil {
        return PhoneNumberUtil.createInstance(application)
    }

    @Provides
    @Singleton
    fun provideLocale(application: Application): Locale {
        return Locale(
            application.getString(R.string.language),
            application.getString(R.string.country)
        )
    }
}