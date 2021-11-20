package com.rio.commerce.android.app.di.module

import android.content.Context
import com.rio.commerce.android.app.BuildConfig
import com.rio.commerce.commonui.UserPreferenceImpl
import com.rio.commerce.core.UserPreference
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class PreferenceModule {

    @Provides
    @Singleton
    @Named("preferenceName")
    fun providePreferenceName(): String {
        return "${BuildConfig.APPLICATION_ID}.preferences"
    }

    @Provides
    @Singleton
    fun providePreference(context: Context, @Named("preferenceName") name: String): UserPreference {
        return UserPreferenceImpl(context, name)
    }
}