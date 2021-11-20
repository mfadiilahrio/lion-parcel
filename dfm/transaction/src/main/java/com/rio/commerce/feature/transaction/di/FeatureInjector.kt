package com.rio.commerce.feature.transaction.di

import com.rio.commerce.android.app.Application
import com.rio.commerce.android.app.di.ModuleInjector
import com.rio.commerce.feature.transaction.di.component.DaggerTransactionComponent
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class FeatureInjector : ModuleInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun inject(app: Application) {
        DaggerTransactionComponent.builder()
            .appComponent(app.appComponent)
            .build()
            .inject(this)
    }

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return androidInjector
    }
}