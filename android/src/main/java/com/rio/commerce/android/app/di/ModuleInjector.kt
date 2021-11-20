package com.rio.commerce.android.app.di

import com.rio.commerce.android.app.Application
import dagger.android.DispatchingAndroidInjector

interface ModuleInjector {

    fun inject(app: Application)

    fun androidInjector(): DispatchingAndroidInjector<Any>
}