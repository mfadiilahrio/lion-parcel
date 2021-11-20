package com.rio.commerce.feature.gallery.di

import com.rio.commerce.android.app.Application
import com.rio.commerce.android.app.di.ModuleInjector
import com.rio.commerce.feature.gallery.di.component.DaggerGalleryComponent
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

class FeatureInjector : ModuleInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun inject(app: Application) {
        DaggerGalleryComponent.builder()
            .appComponent(app.appComponent)
            .build()
            .inject(this)
    }

    override fun androidInjector(): DispatchingAndroidInjector<Any> {
        return androidInjector
    }
}